package org.gooru.nucleus.search.indexers.app.services;

import java.util.Map;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.gooru.nucleus.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class LessonIndexServiceImpl extends BaseIndexService implements LessonIndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(LessonIndexServiceImpl.class);
 
  @Override
  public void setExistingStatisticsData(JsonObject result, Map<String, Object> contentInfoAsMap) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void deleteIndexedLesson(String key, String type) throws Exception {
    try {
      LOGGER.debug("LISI->deleteIndexedLesson : Processing delete lesson for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_DELETED_LESSON);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.LESSON_NOT_DELETED);
      DeleteService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception ex) {
      LOGGER.error("LISI->deleteIndexedLesson : Delete lesson from index failed for question id : " + key + " Exception : " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void indexDocument(String id, JsonObject data) throws Exception {
    if (!data.isEmpty()) {
      try {
        // Get statistics and extracted text data from backup index
        Map<String, Object> contentInfoAsMap = IndexService.instance().getDocument(id, IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO);
        
        setExistingStatisticsData(data, contentInfoAsMap);
        IndexRequest request = new IndexRequest(getIndexName(), getIndexType(), id).source(EsIndexSrcBuilder.get(getIndexType()).buildSource(data), XContentType.JSON); 
        getHighLevelClient().index(request);
      } catch (Exception e) {
          LOGGER.info("Exception while indexing");
          throw new Exception(e);
      }
    }
  }

  @Override
  public void deleteDocument(String id) throws Exception {
    try {
      DeleteRequest delete = new DeleteRequest(getIndexName(), getIndexType(), id); 
      getHighLevelClient().delete(delete);

      // Delete from CI index
      DeleteRequest deleteFromCI = new DeleteRequest(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id); 
      getHighLevelClient().delete(deleteFromCI);
    }
    catch(Exception e){
      LOGGER.error("Failed to delete lesson from index");
      throw new Exception(e);
    }
  }
   
  private String getIndexName(){
    return IndexNameHolder.getIndexName(EsIndex.LESSON);
  }
  
  private String getIndexType(){
    return IndexerConstants.TYPE_LESSON;
  }
}
