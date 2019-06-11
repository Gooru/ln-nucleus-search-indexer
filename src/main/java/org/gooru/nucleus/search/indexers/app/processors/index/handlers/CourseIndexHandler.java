package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.CourseIndexService;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class CourseIndexHandler extends BaseIndexHandler implements IndexHandler {

  private final String indexName;
  
  public CourseIndexHandler() {
    this.indexName = getIndexName();
  }
  
  @Override
  public void indexDocument(String courseId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_COURSE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.COURSE_DATA_NULL);
      //LOGGER.debug("CRIH->indexDocument : getIndexDataCollection() returned:" + result);
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

  @SuppressWarnings("unchecked")
  @Override
  public void increaseCount(String courseId, String field) throws Exception {
    try{
      Map<String, Object> result = IndexService.instance().getDocument(courseId, IndexNameHolder.getIndexName(EsIndex.COURSE), IndexerConstants.COURSE);
      if (result != null && result.get(ScoreConstants.STATISTICS_FIELD) != null) {
        Map<String, Object> indexFields = new HashMap<String, Object>();
        Map<String, Object> statistics = (Map<String, Object>) result.get(ScoreConstants.STATISTICS_FIELD);
        Object remixCount = statistics.get(IndexFields.COURSE_REMIXCOUNT);
        indexFields.put(IndexerConstants.STATISTICS_DOT + IndexFields.COURSE_REMIXCOUNT, incrementValue(remixCount == null ? 0 : remixCount));
        IndexService.instance().indexDocumentByFields(courseId, IndexNameHolder.getIndexName(EsIndex.COURSE), IndexerConstants.COURSE, indexFields);
      }
    }
    catch(Exception e){
      LOGGER.error("CRIH-> update count failed for course : " + courseId + "Exception " + e);
      throw new Exception(e);
    }
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
  
  @SuppressWarnings("rawtypes")
  @Override
  public void indexEnhancedKeywords(String id, Map<String, Object> sourceAsMap) throws Exception {
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

  private String getIndexName() {
    return IndexNameHolder.getIndexName(EsIndex.COURSE);
  }

  private String getIndexType() {
    return IndexerConstants.TYPE_COURSE;
  }
}
