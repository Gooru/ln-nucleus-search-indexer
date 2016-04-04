package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CollectionIndexHandler extends BaseIndexHandler implements IndexHandler {
	
	private final String indexName;
	
	public CollectionIndexHandler() {
		this.indexName = getIndexName();
	}
	
	@Override
	public void indexDocument(String collectionId) throws Exception {
		try{
			ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_COLLECTION);
			JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataCollection();
			ValidationUtil.rejectIfNull(result, ErrorMsgConstants.COLLECTION_DATA_NULL);
			LOGGER.debug("CIH->indexDocument : getIndexDataCollection() returned:" + result);
			IndexService.instance().indexDocuments(collectionId, indexName, getIndexType(), result);
			LOGGER.debug("CIH->indexDocument : Indexed collection for collection id : " + collectionId);
		}  
		catch(Exception ex){
			LOGGER.error("CIH->Re-index failed for collection : " + collectionId +" Exception " +ex);
			throw new Exception(ex);
		}
	}

	@Override
	public void indexDocuments(JsonObject idsJson) throws Exception {
		try{
			ValidationUtil.rejectIfNull(idsJson, ErrorMsgConstants.COLLECTION_IDS_NULL);
			JsonArray ids = idsJson.getJsonArray(IndexerConstants.COLLECTION_IDS);	
			if(ids != null && ids.size() > 0){
				LOGGER.debug("CIH->indexDocuments : Processing received ids array size : " + ids.size());
				Iterator<Object> iter = idsJson.getJsonArray(IndexerConstants.COLLECTION_IDS).iterator();
				while(iter.hasNext()){
					indexDocument((String)iter.next());
				}
				LOGGER.debug("CIH->indexDocuments : Successfully indexed all the collections/assessments");
			}
			else {
				LOGGER.debug("CIH->indexDocuments : Zero collections/assessments in the consumed data !!");
			}
		}
		catch(Exception ex){
			LOGGER.error("CIH->indexDocuments : Re-index collections/assessments failed. Exception : " + ex);
			throw new Exception(ex);
		}
		
	}

	@Override
	public void deleteIndexedDocument(String collectionId) throws Exception {
		try{
			LOGGER.debug("CIH->deleteIndexedDocument : Processing delete collection for id : "+collectionId);
			IndexService.instance().deleteDocuments(collectionId, indexName, getIndexType());
		}
		catch(Exception ex){
			LOGGER.error("CIH->deleteIndexedDocument : Delete collection from index failed for collection id : " +collectionId +" Exception : " + ex);
			throw new Exception(ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getScoreValues(String collectionId){
		Map<String, Object> result = IndexService.instance().getDocument(collectionId, indexName, getIndexType());
		if(result == null || result.get(ScoreConstants.STATISTICS_FIELD) == null){
			throw new RuntimeException("Invalid Request");
		}
		
		Map<String, Object> statisticsMap = (Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD);
		Map<String, Object> scoreFieldsValues = statisticsMap;
		Map<String, Object> taxonomy = (Map<String, Object>) result.get(ScoreConstants.TAXONOMY_FIELD);
		scoreFieldsValues.put(ScoreConstants.TAX_HAS_STANDARD, taxonomy.get(ScoreConstants.TAX_HAS_STANDARD));
		scoreFieldsValues.put(ScoreConstants.ORIGINAL_CONTENT_FIELD, result.get(ScoreConstants.ORIGINAL_CONTENT_FIELD));
		return scoreFieldsValues;
	}
	
	@Override
	public void increaseCount(String collectionId, String field) throws Exception {
		try{
			handleCount(collectionId, field, 0, ScoreConstants.OPERATION_TYPE_INCR);
		}
		catch(Exception e){
			LOGGER.error("CIH->increaseCount : Update fields values failed for fields : "+ field.toString() + " collection id :"+collectionId);
			throw new Exception(e);
		}
	}

	private void indexDocumentByFields(Map<String, Object> fieldsMap, Map<String, Object> rankingFields, String collectionId) throws Exception {
		//Calculate PC weight 
		double pcWeight = PCWeightUtil.getCollectionPCWeight(new ScoreFields(rankingFields));
		LOGGER.debug("New PC weight : "+pcWeight+" for collection id : " + collectionId);
		fieldsMap.put("preComputedWeight", pcWeight);
		IndexService.instance().indexDocumentByFields(collectionId, indexName, getIndexType(), fieldsMap);
	}
	
	@Override
	public void decreaseCount(String collectionId, String field) throws Exception {
		try{
			handleCount(collectionId, field, 0, ScoreConstants.OPERATION_TYPE_DECR);
		}
		catch(Exception e){
			LOGGER.error("CIH->decreaseCount : Update fields values failed for fields : "+ field.toString() + " collection id :"+collectionId);
			throw new Exception(e);
		}
	}

	private void handleCount(String collectionId, String field, int count, String operationType) throws Exception {
		try{
			Map<String, Object> fieldsMap = new HashMap<String, Object>();
			Map<String, Object> scoreValues = getScoreValues(collectionId);
			handleCount(collectionId, field, operationType, count, scoreValues, fieldsMap);
			indexDocumentByFields(fieldsMap, scoreValues, collectionId);
		}
		catch(Exception e){
			LOGGER.error("CIH->handleCount : Update fields values failed for fields : "+ field+ " collection id :"+collectionId);
			throw new Exception(e);
		}
	}

	@Override
	public void updateCount(String collectionId, String field, int count) throws Exception {
		try{
			handleCount(collectionId, field, count, ScoreConstants.OPERATION_TYPE_UPDATE);
		}
		catch(Exception e){
			LOGGER.error("CIH->updateCount : Update fields values failed for fields : "+ field+ " collection id :"+collectionId);
			throw new Exception(e);
		}
	}

	@Override
	public void updateViewCount(String entityId, Long viewCount) throws Exception {
		// TODO Auto-generated method stub
		
	}

	private String getIndexName(){
		return IndexNameHolder.getIndexName(EsIndex.COLLECTION);
	}
	
	private String getIndexType(){
		return IndexerConstants.TYPE_COLLECTION;
	}



}
