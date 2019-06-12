package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.LessonIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class LessonIndexHandler extends BaseIndexHandler implements IndexHandler {

  private final String indexName;
  
  public LessonIndexHandler() {
    this.indexName = getIndexName();
  }
  
  @Override
  public void indexDocument(String lessonId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(lessonId, ExecuteOperationConstants.GET_LESSON);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.LESSON_DATA_NULL);
      LessonIndexService.instance().indexDocument(lessonId, result);
      LOGGER.debug("LIH->indexDocument : Indexed lesson id : " + lessonId);
    } catch (Exception ex) {
      LOGGER.error("LIH->Re-index failed for lesson : " + lessonId + " Exception " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void indexDocuments(JsonObject idsJson) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteIndexedDocument(String lessonId) throws Exception {
    try {
      LOGGER.debug("LIH->deleteIndexedDocument : Processing delete lesson for id : " + lessonId);
      ProcessorContext context = new ProcessorContext(lessonId, ExecuteOperationConstants.GET_DELETED_LESSON);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.LESSON_NOT_DELETED);
      LessonIndexService.instance().deleteDocument(lessonId);
    }
    catch(Exception e){
      LOGGER.error("LIH-> Delete failed for lesson : " + lessonId + " Exception " + e);
      throw new Exception(e);
    }
  }

  @Override
  public void increaseCount(String lessonId, String field) throws Exception {
    
  }

  @Override
  public void decreaseCount(String entityId, String field) throws Exception {
    
  }

  @Override
  public void updateCount(String entityId, String field, int count) throws Exception {
    
  }

  @Override
  public void updateViewCount(String entityId, Long viewCount) {
    
  }

  @Override
  public void updateUserDocuments(String userId) throws Exception {
    
  }
  
  @Override
  public void indexEnhancedKeywords(String id, Map<String, Object> sourceAsMap) throws Exception {
    
  }

  private String getIndexName() {
    return IndexNameHolder.getIndexName(EsIndex.LESSON);
  }

}
