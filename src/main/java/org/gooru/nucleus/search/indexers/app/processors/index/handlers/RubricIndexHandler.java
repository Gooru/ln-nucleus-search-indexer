package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.CourseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class RubricIndexHandler extends BaseIndexHandler implements IndexHandler {

  private final String indexName;
  
  public RubricIndexHandler() {
    this.indexName = getIndexName();
  }
  
  private String getIndexName() {
    return IndexNameHolder.getIndexName(EsIndex.RUBRIC);
  }

  private String getIndexType() {
    return IndexerConstants.TYPE_RUBRIC;
  }

  @Override
  public void indexDocument(String rubricId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(rubricId, ExecuteOperationConstants.GET_RUBRIC);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.RUBRIC_DATA_NULL);
      LOGGER.debug("RuIH->indexDocument : getIndexDataRubric() returned:" + result);
      CourseIndexService.instance().indexDocument(rubricId, result);
      LOGGER.debug("RuIH->indexDocument : Indexed rubric id : " + rubricId);
    } catch (Exception ex) {
      LOGGER.error("RuIH->Re-index failed for rubric : " + rubricId + " Exception " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void indexDocuments(JsonObject idsJson) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteIndexedDocument(String rubricId) throws Exception {
    try{
      LOGGER.debug("RuIH->deleteIndexedDocument : Processing delete rubric for id : " + rubricId);
      ProcessorContext context = new ProcessorContext(rubricId, ExecuteOperationConstants.GET_DELETED_RUBRIC);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.RUBRIC_NOT_DELETED);
      CourseIndexService.instance().deleteDocument(rubricId);
    }
    catch(Exception e){
      LOGGER.error("RuIH-> Delete failed for rubric : " + rubricId + " Exception " + e);
      throw new Exception(e);
    }
  }

  @Override
  public void increaseCount(String entityId, String field) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void decreaseCount(String entityId, String field) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateCount(String entityId, String field, int count) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateViewCount(String entityId, Long viewCount) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateUserDocuments(String userId) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void indexEnhancedKeywords(String id, Map<String, Object> sourceAsMap) throws Exception {
    // TODO Auto-generated method stub
    
  }

}
