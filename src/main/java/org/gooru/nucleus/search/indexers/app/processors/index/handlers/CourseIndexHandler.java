package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.CourseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class CourseIndexHandler  implements IndexHandler {


  @Override
  public void indexDocument(String courseId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_COURSE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.COURSE_DATA_NULL);
      LOGGER.debug("CRIH->indexDocument : getIndexDataCollection() returned:" + result);
      CourseIndexService.instance().indexDocument(courseId, result);
      LOGGER.debug("CRIH->indexDocument : Indexed course for course id : " + courseId);
    } catch (Exception ex) {
      LOGGER.error("CRIH->Re-index failed for course : " + courseId + " Exception " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void indexDocuments(JsonObject idsJson) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteIndexedDocument(String courseId) throws Exception {
    try{
      LOGGER.debug("CRIH->deleteIndexedDocument : Processing delete course for id : " + courseId);
      ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_DELETED_COURSE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.COURSE_NOT_DELETED);
      CourseIndexService.instance().deleteDocument(courseId);
    }
    catch(Exception e){
      LOGGER.error("CRIH-> Delete failed for course : " + courseId + " Exception " + e);
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

}
