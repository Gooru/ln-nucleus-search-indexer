package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.util.Iterator;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nuclues.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nuclues.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CollectionIndexHandler implements IndexHandler {
	
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

	@Override
	public void indexDocmentPartial(JsonObject json) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private String getIndexName(){
		return IndexNameHolder.getIndexName(EsIndex.COLLECTION);
	}
	
	private String getIndexType(){
		return IndexerConstants.TYPE_COLLECTION;
	}


}
