package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

public class QuestionAndResourceReferenceIndexHandler extends BaseIndexHandler implements IndexHandler {

  private final String indexName;

  public QuestionAndResourceReferenceIndexHandler() {
    this.indexName = getIndexName();
  }

  @Override
  public void indexDocument(String resourceId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(resourceId, ExecuteOperationConstants.GET_QUESTION_OR_RESOURCE_REFERENCE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.QUESTION_DATA_NULL);
      //LOGGER.debug("QIH->indexDocument: getIndexDataContent() returned:" + result);
      //Extract text while indexing ==>> IndexService.instance().buildInfoIndex(resourceId, result);
      IndexService.instance().indexDocuments(resourceId, indexName, getIndexType(), result);
    } catch (Exception ex) {
      LOGGER.error("QIH->indexDocument: Re-index failed for question : " + resourceId + " Exception " + ex);
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
      LOGGER.debug("QIH->deleteIndexedDocument : Processing delete question for id : " + resourceId);
      ProcessorContext context = new ProcessorContext(resourceId, ExecuteOperationConstants.GET_DELETED_QUESTION_OR_RESOURCE_REFERENCE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.QUESTION_NOT_DELETED);
      IndexService.instance().deleteDocuments(resourceId, indexName, getIndexType());
    } catch (Exception ex) {
      LOGGER.error("QIH->deleteIndexedDocument : Delete question from index failed for question id : " + resourceId + " Exception : " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void increaseCount(String resourceId, String field) throws Exception {
    try {
      handleCount(resourceId, field, 0, ScoreConstants.OPERATION_TYPE_INCR);
    } catch (Exception e) {
      LOGGER.error("QIH->increaseCount : Update fields values failed for fields : " + field + " question id :" + resourceId);
      throw new Exception(e);
    }
  }

  @Override
  public void decreaseCount(String resourceId, String field) throws Exception {
    try {
      handleCount(resourceId, field, 0, ScoreConstants.OPERATION_TYPE_DECR);
    } catch (Exception e) {
      LOGGER.error("QIH->decreaseCount : Update fields values failed for fields : " + field + " question id :" + resourceId);
      throw new Exception(e);
    }
  }

  @Override
  public void updateCount(String resourceId, String field, int count) throws Exception {
    try {
      handleCount(resourceId, field, count, ScoreConstants.OPERATION_TYPE_UPDATE);
    } catch (Exception e) {
      LOGGER.error("QIH->updateCount : Update fields values failed for fields : " + field + " resource id :" + resourceId);
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
      LOGGER.error("QIH->handleCount : Update fields values failed for fields : " + field + " resource id :" + resourceId);
      throw new Exception(e);
    }
  }

  private String getIndexName() {
    return IndexNameHolder.getIndexName(EsIndex.RESOURCE);
  }

  private String getIndexType() {
    return IndexerConstants.TYPE_QUESTION;
  }

  @Override
  public void updateUserDocuments(String userId) throws Exception {
    try {
      LOGGER.debug("QIH->updateUserDocuments : Processing update user questions  : " + userId);
      indexUserQuestions(userId);
    } catch (Exception ex) {
      LOGGER.error("QIH->updateUserDocuments : Re-index user questions failed for user : " + userId + " Exception : " + ex);
      throw new Exception(ex);
    }
    
    try {
        LOGGER.debug("QIH->updateUserDocuments : Processing update user resource references  : " + userId);
        indexUserResourceReferences(userId);
      } catch (Exception ex) {
        LOGGER.error("QIH->updateUserDocuments : Re-index user resource references failed for user : " + userId + " Exception : " + ex);
        throw new Exception(ex);
      }

  }
  
  private void indexUserResourceReferences(String userId) {
      ProcessorContext questionContext = new ProcessorContext(userId, ExecuteOperationConstants.GET_USER_RESOURCE_REFERENCES);
      JsonObject result = RepoBuilder.buildIndexerRepo(questionContext).getIndexDataContent();
      if(result != null && result.getJsonArray(IndexerConstants.RESOURCE_REFERENCES) != null && result.getJsonArray(IndexerConstants.RESOURCE_REFERENCES).size() > 0){
        IndexService.instance().bulkIndexDocuments(result.getJsonArray(IndexerConstants.RESOURCE_REFERENCES), getIndexType(), getIndexName());
      } else {
        LOGGER.debug("QIH->indexUserResourceReferences : DB returned 0 copied resources,  user Id  : " + userId);
      }
    }
  
  private void indexUserQuestions(String userId) {
    ProcessorContext questionContext = new ProcessorContext(userId, ExecuteOperationConstants.GET_USER_QUESTIONS);
    JsonObject questionResult = RepoBuilder.buildIndexerRepo(questionContext).getIndexDataContent();
    if(questionResult != null && questionResult.getJsonArray(IndexerConstants.QUESTIONS) != null && questionResult.getJsonArray(IndexerConstants.QUESTIONS).size() > 0){
      IndexService.instance().bulkIndexDocuments(questionResult.getJsonArray(IndexerConstants.QUESTIONS), getIndexType(), getIndexName());
    } else {
      LOGGER.debug("QIH->indexUserQuestions : DB returned 0 questions,  user Id  : " + userId);
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void indexEnhancedKeywords( String id, Map<String, Object> sourceAsMap) throws Exception {
    Map<String, Object> contentSource = new HashMap<>();
    Map<String, Object> contentInfoSource = new HashMap<>();
    Map<String, Object> resourceInfo = new HashMap<>();
    for (String key : sourceAsMap.keySet()) {
      if (!((List) sourceAsMap.get(key)).isEmpty()) {
        contentSource.put(IndexerConstants.INFO_WATSON_TAGS_DOT + key, sourceAsMap.get(key));
        contentSource.put(IndexerConstants.INDEX_UPDATED_TIME, new SimpleDateFormat(IndexerConstants.DATE_FORMAT).format(new Date()));
        resourceInfo.put(key, sourceAsMap.get(key));
      }
    }
    if (!resourceInfo.isEmpty()) {
      Map<String, Object> watsonTags = new HashMap<>();
      watsonTags.put(IndexerConstants.WATSON_TAGS, resourceInfo);
      contentInfoSource.put(IndexerConstants.RESOURCE_INFO, watsonTags);
      contentInfoSource.put(IndexerConstants.INDEX_UPDATED_TIME, new SimpleDateFormat(IndexerConstants.DATE_FORMAT).format(new Date()));
      IndexService.instance().indexDocumentByField(id, indexName, getIndexType(), contentSource, contentInfoSource);
    }
  }
}
