package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.constants.*;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import java.util.HashMap;
import java.util.Map;

public class CollectionIndexHandler extends BaseIndexHandler implements IndexHandler {

  private final String indexName;

  public CollectionIndexHandler() {
    this.indexName = getIndexName();
  }

  @Override
  public void indexDocument(String collectionId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_COLLECTION);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.COLLECTION_DATA_NULL);
      LOGGER.debug("CIH->indexDocument : getIndexDataCollection() returned:" + result);
      IndexService.instance().indexDocuments(collectionId, indexName, getIndexType(), result);
      LOGGER.debug("CIH->indexDocument : Indexed collection for collection id : " + collectionId);
    } catch (Exception ex) {
      LOGGER.error("CIH->Re-index failed for collection : " + collectionId + " Exception " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void indexDocuments(JsonObject idsJson) throws Exception {
    try {
      ValidationUtil.rejectIfNull(idsJson, ErrorMsgConstants.COLLECTION_IDS_NULL);
      JsonArray ids = idsJson.getJsonArray(IndexerConstants.COLLECTION_IDS);
      if (ids != null && ids.size() > 0) {
        LOGGER.debug("CIH->indexDocuments : Processing received ids array size : " + ids.size());
        for (Object o : idsJson.getJsonArray(IndexerConstants.COLLECTION_IDS)) {
          indexDocument((String) o);
        }
        LOGGER.debug("CIH->indexDocuments : Successfully indexed all the collections/assessments");
      } else {
        LOGGER.debug("CIH->indexDocuments : Zero collections/assessments in the consumed data !!");
      }
    } catch (Exception ex) {
      LOGGER.error("CIH->indexDocuments : Re-index collections/assessments failed. Exception : " + ex);
      throw new Exception(ex);
    }

  }

  @Override
  public void deleteIndexedDocument(String collectionId) throws Exception {
    try {
      LOGGER.debug("CIH->deleteIndexedDocument : Processing delete collection for id : " + collectionId);
      ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_DELETED_COLLECTION);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.COLLECTION_NOT_DELETED);
      IndexService.instance().deleteDocuments(collectionId, indexName, getIndexType());
    } catch (Exception ex) {
      LOGGER.error("CIH->deleteIndexedDocument : Delete collection from index failed for collection id : " + collectionId + " Exception : " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void increaseCount(String collectionId, String field) throws Exception {
    try {
      handleCount(collectionId, field, 0, ScoreConstants.OPERATION_TYPE_INCR);
    } catch (Exception e) {
      LOGGER.error("CIH->increaseCount : Update fields values failed for fields : " + field + " collection id :" + collectionId);
      throw new Exception(e);
    }
  }

  @Override
  public void decreaseCount(String collectionId, String field) throws Exception {
    try {
      handleCount(collectionId, field, 0, ScoreConstants.OPERATION_TYPE_DECR);
    } catch (Exception e) {
      LOGGER.error("CIH->decreaseCount : Update fields values failed for fields : " + field + " collection id :" + collectionId);
      throw new Exception(e);
    }
  }

  @Override
  public void updateCount(String collectionId, String field, int count) throws Exception {
    try {
      handleCount(collectionId, field, count, ScoreConstants.OPERATION_TYPE_UPDATE);
    } catch (Exception e) {
      LOGGER.error("CIH->updateCount : Update fields values failed for fields : " + field + " collection id :" + collectionId);
      throw new Exception(e);
    }
  }

  @Override
  public void updateViewCount(String entityId, Long viewCount) {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getScoreValues(String collectionId) {
    Map<String, Object> result = IndexService.instance().getDocument(collectionId, indexName, getIndexType());
    if (result == null || result.get(ScoreConstants.STATISTICS_FIELD) == null) {
      LOGGER.debug("Collection/statistics data not available in index !! - Collection id :" + collectionId);
      return null;
    }
    Map<String,Object> rankingFields = (Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD);
    Map<String, Object> taxonomy = (Map<String, Object>) result.get(ScoreConstants.TAXONOMY_FIELD);
    
    int hasNoStandard = 1;
    
    if (taxonomy != null && taxonomy.get(EntityAttributeConstants.TAXONOMY_HAS_STD) != null && (int)taxonomy.get(EntityAttributeConstants.TAXONOMY_HAS_STD) == 1) {
      hasNoStandard = 0;
    }

    rankingFields.put(ScoreConstants.TAX_HAS_NO_STANDARD, hasNoStandard);
    rankingFields.put(ScoreConstants.ORIGINAL_CONTENT_FIELD, result.get(ScoreConstants.ORIGINAL_CONTENT_FIELD));
    rankingFields.put(ScoreConstants.PUBLISH_STATUS, result.get(ScoreConstants.PUBLISH_STATUS));
    rankingFields.put(ScoreConstants.DESCRIPTION_FIELD, result.get(ScoreConstants.LEARNING_OBJ));
    return rankingFields;
  }

  private void indexDocumentByFields(Map<String, Object> fieldsMap, Map<String, Object> rankingFields, String collectionId) throws Exception {
    //Calculate PC weight
    double pcWeight = PCWeightUtil.getCollectionPCWeight(new ScoreFields(rankingFields));
    LOGGER.debug("New PC weight : " + pcWeight + " for collection id : " + collectionId);
    fieldsMap.put(ScoreConstants.PC_WEIGHT_FIELD, pcWeight);
    IndexService.instance().indexDocumentByFields(collectionId, indexName, getIndexType(), fieldsMap);
  }

  private void handleCount(String collectionId, String field, int count, String operationType) throws Exception {
    try {
      Map<String, Object> fieldsMap = new HashMap<>();
      Map<String, Object> scoreValues = getScoreValues(collectionId);
      if(scoreValues != null){
        handleCount(collectionId, field, operationType, count, scoreValues, fieldsMap);
        indexDocumentByFields(fieldsMap, scoreValues, collectionId);
      }
    } catch (Exception e) {
      LOGGER.error("CIH->handleCount : Update fields values failed for fields : " + field + " collection id :" + collectionId, e);
      throw new Exception(e);
    }
  }

  private String getIndexName() {
    return IndexNameHolder.getIndexName(EsIndex.COLLECTION);
  }

  private String getIndexType() {
    return IndexerConstants.TYPE_COLLECTION;
  }

  @Override
  public void updateUserDocuments(String userId) throws Exception {
    try {
      LOGGER.debug("CIH->updateUserDocuments : Processing update user collections  : " + userId);
      ProcessorContext context = new ProcessorContext(userId, ExecuteOperationConstants.GET_USER_COLLECTIONS);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      if(result != null && result.getJsonArray(IndexerConstants.COLLECTIONS) != null && result.getJsonArray(IndexerConstants.COLLECTIONS).size() > 0){
        IndexService.instance().bulkIndexDocuments(result.getJsonArray(IndexerConstants.COLLECTIONS), getIndexType(), getIndexName());
      }
      else {
        LOGGER.debug("CIH->updateUserDocuments : DB returned 0 collections,  user Id  : " + userId);
      }
    } catch (Exception ex) {
      LOGGER.error("CIH->updateUserDocuments : Re-index user collections failed for user : " + userId + " Exception : " + ex);
      throw new Exception(ex);
    }
  }


}
