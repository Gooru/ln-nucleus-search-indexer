package org.gooru.nucleus.search.indexers.app.services;

import java.util.Map;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.gooru.nucleus.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class CourseIndexServiceImpl extends BaseIndexService implements CourseIndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseIndexServiceImpl.class);

  @Override
  public void indexDocument(String id, JsonObject data) throws Exception {
    if (!data.isEmpty()) {
      try {
        // Get statistics and extracted text data from backup index
        Map<String, Object> contentInfoAsMap = IndexService.instance().getDocument(id, IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO);
        
        setExistingStatisticsData(data, contentInfoAsMap);
        IndexRequest request = new IndexRequest(getIndexName(), IndexerConstants.TYPE_COURSE, id).source(EsIndexSrcBuilder.get(IndexerConstants.TYPE_COURSE).buildSource(data), XContentType.JSON); 
        getHighLevelClient().index(request);
      } catch (Exception e) {
          LOGGER.info("Exception while indexing");
          throw new Exception(e);
      }
    }
  }
  
  private String getIndexName(){
    return IndexNameHolder.getIndexName(EsIndex.COURSE);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void setExistingStatisticsData(JsonObject source, Map<String, Object> contentInfoAsMap) {
    long viewsCount = 0L;
    int remixCount = 0;
    int collaboratorCount = 0;

    if (contentInfoAsMap != null) {
      Map<String, Object> statisticsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.STATISTICS);
      if(statisticsMap != null){
        LOGGER.debug("statistics index data : " + statisticsMap);
        viewsCount = getLong(statisticsMap.get(IndexFields.VIEWS_COUNT));
        remixCount = getInteger(statisticsMap.get(IndexFields.COURSE_REMIXCOUNT));
        collaboratorCount = getInteger(statisticsMap.get(IndexFields.COLLABORATOR_COUNT));
      }
    }

    source.put(IndexFields.VIEWS_COUNT, viewsCount);
    source.put(IndexFields.COURSE_REMIXCOUNT, remixCount);
    source.put(IndexFields.COLLABORATOR_COUNT, collaboratorCount);
  }

  @Override
  public void deleteDocument(String id) throws Exception {
    try {
      DeleteRequest delete = new DeleteRequest(getIndexName(), IndexerConstants.TYPE_COURSE, id); 
      getHighLevelClient().delete(delete);

      // Delete from CI index
      DeleteRequest deleteFromCI = new DeleteRequest(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id); 
      getHighLevelClient().delete(deleteFromCI);
    }
    catch(Exception e){
      LOGGER.error("Failed to delete course from index");
      throw new Exception(e);
    }
  }
  
  @Override
  public void deleteIndexedCourse(String key, String type) throws Exception {
    try {
      LOGGER.debug("CISI->deleteIndexedCourse : Processing delete course for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_DELETED_COURSE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.COURSE_NOT_DELETED);
      IndexService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception e) {
      LOGGER.error("CISI-> Delete failed for course : " + key + " Exception " + e);
      throw new Exception(e);
    }
  }

}


