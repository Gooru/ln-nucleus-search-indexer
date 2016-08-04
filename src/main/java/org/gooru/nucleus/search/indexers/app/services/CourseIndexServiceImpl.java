package org.gooru.nucleus.search.indexers.app.services;

import java.util.Map;

import org.gooru.nucleus.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
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
        
        getClient().prepareIndex(getIndexName(), IndexerConstants.TYPE_COURSE, id).setSource(EsIndexSrcBuilder.get(IndexerConstants.TYPE_COURSE).buildSource(data)).execute()
                .actionGet();
      } catch (Exception e) {
          LOGGER.info("Exception while indexing");
          throw new Exception(e);
      }
    }
  }
  
  private String getIndexName(){
    return IndexNameHolder.getIndexName(EsIndex.COURSE);
  }
  
  private void setExistingStatisticsData(JsonObject source, Map<String, Object> contentInfoAsMap) {
    long viewsCount = 0L;
    int remixCount = 0;

    if (contentInfoAsMap != null) {
      Map<String, Object> statisticsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.STATISTICS);
      if(statisticsMap != null){
        LOGGER.debug("statistics index data : " + statisticsMap);
        viewsCount = getLong(statisticsMap.get(IndexFields.VIEWS_COUNT));
        remixCount = getInteger(statisticsMap.get(IndexFields.COURSE_REMIXCOUNT));
      }
    }

    source.put(IndexFields.VIEWS_COUNT, viewsCount);
    source.put(IndexFields.COURSE_REMIXCOUNT, remixCount);
  }

  @Override
  public void deleteDocument(String id) throws Exception {
    try {
      getClient().prepareDelete(getIndexName(), IndexerConstants.TYPE_COURSE, id).execute().actionGet();
      
      // Delete from CI index
      getClient().prepareDelete(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id).execute().actionGet();
    }
    catch(Exception e){
      LOGGER.error("Failed to delete course from index");
      throw new Exception(e);
    }
  }

}


