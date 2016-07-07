package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class ResourceIndexHandler extends BaseIndexHandler implements IndexHandler {

  private final String indexName;

  public ResourceIndexHandler() {
    this.indexName = getIndexName();
  }

  @Override
  public void indexDocument(String resourceId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(resourceId, ExecuteOperationConstants.GET_RESOURCE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.RESOURCE_DATA_NULL);
      LOGGER.debug("RIH->indexDocument: getIndexDataContent() returned:" + result);
      //Extract text while indexing ==>> IndexService.instance().buildInfoIndex(resourceId, result);
      IndexService.instance().indexDocuments(resourceId, indexName, getIndexType(), result);
    } catch (Exception ex) {
      LOGGER.error("RIH->indexDocument: Re-index failed for resource : " + resourceId + " Exception " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void indexDocuments(JsonObject idsJson) throws Exception {
    // TODO Auto-generated method stub
  }

  @Override
  public void deleteIndexedDocument(String resourceId) throws Exception {
    try {
      LOGGER.debug("CIH->deleteIndexedDocument : Processing delete resource for id : " + resourceId);
      ProcessorContext context = new ProcessorContext(resourceId, ExecuteOperationConstants.GET_DELETED_RESOURCE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.RESOURCE_NOT_DELETED);
      IndexService.instance().deleteDocuments(resourceId, indexName, getIndexType(), result.getString(EntityAttributeConstants.CREATED_AT));
    } catch (Exception ex) {
      LOGGER.error("CIH->deleteIndexedDocument : Delete resource from index failed for resource id : " + resourceId + " Exception : " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void increaseCount(String resourceId, String field) throws Exception {
    try {
      handleCount(resourceId, field, 0, ScoreConstants.OPERATION_TYPE_INCR);
    } catch (Exception e) {
      LOGGER.error("RIH->increaseCount : Update fields values failed for fields : " + field + " resource id :" + resourceId);
      throw new Exception(e);
    }
  }

  @Override
  public void decreaseCount(String resourceId, String field) throws Exception {
    try {
      handleCount(resourceId, field, 0, ScoreConstants.OPERATION_TYPE_DECR);
    } catch (Exception e) {
      LOGGER.error("RIH->decreaseCount : Update fields values failed for fields : " + field + " resource id :" + resourceId);
      throw new Exception(e);
    }
  }

  @Override
  public void updateCount(String resourceId, String field, int count) throws Exception {
    try {
      handleCount(resourceId, field, count, ScoreConstants.OPERATION_TYPE_UPDATE);
    } catch (Exception e) {
      LOGGER.error("RIH->updateCount : Update fields values failed for fields : " + field + " resource id :" + resourceId);
      throw new Exception(e);
    }
  }

  @Override
  public void updateViewCount(String resourceId, Long viewCount) {
    // TODO Auto-generated method stub
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getScoreValues(String resourceId) {
    Map<String, Object> result = IndexService.instance().getDocument(resourceId, indexName, getIndexType());
    if (result == null || result.get(ScoreConstants.STATISTICS_FIELD) == null) {
      LOGGER.debug("Resource/statistics data not available in index !! - Resource id :" + resourceId);
      return null;
    }

    Map<String, Object> rankingFields = (Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD);
    Map<String, Object> taxonomy = (Map<String, Object>) result.get(ScoreConstants.TAXONOMY_FIELD);
    
    int hasNoStandard = 1;
    if (taxonomy != null && taxonomy.get(EntityAttributeConstants.TAXONOMY_HAS_STD) != null && (int)taxonomy.get(EntityAttributeConstants.TAXONOMY_HAS_STD) == 1) {
      hasNoStandard = 0;
    }

    int oer = 0;
    Map<String, Object> infoMap = (Map<String, Object>) result.get(IndexFields.INFO);
    if(infoMap != null && infoMap.get(IndexFields.OER) != null ){
      oer = (int) infoMap.get(IndexFields.OER);
    }

    rankingFields.put(ScoreConstants.TAX_HAS_NO_STANDARD, hasNoStandard);
    rankingFields.put(ScoreConstants.PUBLISH_STATUS, result.get(ScoreConstants.PUBLISH_STATUS));
    rankingFields.put(ScoreConstants.DESCRIPTION_FIELD, result.get(ScoreConstants.DESCRIPTION_FIELD));
    rankingFields.put(ScoreConstants.RESOURCE_URL_FIELD, result.get(ScoreConstants.RESOURCE_URL_FIELD));
    rankingFields.put(ScoreConstants.OER, oer);

    return rankingFields;
  }

  private void indexDocumentByFields(Map<String, Object> fieldsMap, Map<String, Object> rankingFields, String resourceId) throws Exception {
    //Calculate PC weight
    double pcWeight = PCWeightUtil.getResourcePcWeight(new ScoreFields(rankingFields));
    LOGGER.debug("New PC weight : " + pcWeight + " for resource id : " + resourceId);
    fieldsMap.put(ScoreConstants.PC_WEIGHT_FIELD, pcWeight);
    IndexService.instance().indexDocumentByFields(resourceId, indexName, getIndexType(), fieldsMap);
  }

  private void handleCount(String resourceId, String field, int count, String operationType) throws Exception {
    try {
      Map<String, Object> fieldsMap = new HashMap<>();
      Map<String, Object> scoreValues = getScoreValues(resourceId);
      if(scoreValues != null){
        handleCount(resourceId, field, operationType, count, scoreValues, fieldsMap);
        indexDocumentByFields(fieldsMap, scoreValues, resourceId);
      }
    } catch (Exception e) {
      LOGGER.error("RIH->handleCount : Update fields values failed for fields : " + field + " resource id :" + resourceId);
      throw new Exception(e);
    }
  }

  private String getIndexName() {
    return IndexNameHolder.getIndexName(EsIndex.RESOURCE);
  }

  private String getIndexType() {
    return IndexerConstants.TYPE_RESOURCE;
  }

  @Override
  public void updateUserDocuments(String userId) throws Exception {
    try {
      LOGGER.debug("RIH->updateUserDocuments : Processing update user documents  : " + userId);
      ProcessorContext context = new ProcessorContext(userId, ExecuteOperationConstants.GET_USER_RESOURCES);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      if(result != null && result.getJsonArray(IndexerConstants.RESOURCES) != null && result.getJsonArray(IndexerConstants.RESOURCES).size() > 0){
        IndexService.instance().bulkIndexDocuments(result.getJsonArray(IndexerConstants.RESOURCES), getIndexType(), getIndexName());
      }
      else {
        LOGGER.debug("RIH->updateUserDocuments : DB returned 0 resources,  user Id  : " + userId);
      }
    } catch (Exception ex) {
      LOGGER.error("RIH->updateUserDocuments : Re-index user resources failed for user : " + userId + " Exception : " + ex);
      throw new Exception(ex);
    }

  }

}
