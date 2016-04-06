package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

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
  public void deleteIndexedDocument(String documentId) throws Exception {
    // TODO Auto-generated method stub
  }

  @Override
  public void increaseCount(String resourceId, String field) throws Exception {
    try {
      handleCount(resourceId, field, 0, ScoreConstants.OPERATION_TYPE_INCR);
    } catch (Exception e) {
      LOGGER.error("RIH->increaseCount : Update fields values failed for fields : " + field.toString() + " resource id :" + resourceId);
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
  public void updateViewCount(String resourceId, Long viewCount) throws Exception {
    // TODO Auto-generated method stub
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getScoreValues(String resourceId) {
    Map<String, Object> result = IndexService.instance().getDocument(resourceId, indexName, getIndexType());
    if (result == null || result.get(ScoreConstants.STATISTICS_FIELD) == null) {
      throw new RuntimeException("Invalid Request");
    }

    Map<String, Object> taxonomy = (Map<String, Object>) result.get(ScoreConstants.TAXONOMY_FIELD);
    ((Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD))
      .put(ScoreConstants.TAX_HAS_STANDARD, taxonomy.get(ScoreConstants.TAX_HAS_STANDARD));
    ((Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD))
      .put(ScoreConstants.RESOURCE_URL_FIELD, result.get(ScoreConstants.RESOURCE_URL_FIELD));
    ((Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD))
      .put(ScoreConstants.DESCRIPTION_FIELD, result.get(ScoreConstants.DESCRIPTION_FIELD));
    return (Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD);
  }

  private void indexDocumentByFields(Map<String, Object> fieldsMap, Map<String, Object> rankingFields, String resourceId) throws Exception {
    //Calculate PC weight
    double pcWeight = PCWeightUtil.getResourcePcWeight(new ScoreFields(rankingFields));
    LOGGER.debug("New PC weight : " + pcWeight + " for resource id : " + resourceId);
    fieldsMap.put("preComputedWeight", pcWeight);
    IndexService.instance().indexDocumentByFields(resourceId, indexName, getIndexType(), fieldsMap);
  }

  private void handleCount(String resourceId, String field, int count, String operationType) throws Exception {
    try {
      Map<String, Object> fieldsMap = new HashMap<String, Object>();
      Map<String, Object> scoreValues = getScoreValues(resourceId);
      handleCount(resourceId, field, operationType, count, scoreValues, fieldsMap);
      indexDocumentByFields(fieldsMap, scoreValues, resourceId);
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

}
