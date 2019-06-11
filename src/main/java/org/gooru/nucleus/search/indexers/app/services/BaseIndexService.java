package org.gooru.nucleus.search.indexers.app.services;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchRegistry;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ContentInfoEio;
import org.gooru.nucleus.search.indexers.app.index.model.CourseStatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.util.StringUtil;

import io.vertx.core.json.JsonObject;

public class BaseIndexService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndexService.class);
  public static final String WATSON_TAGS_FIELD = "resourceInfo.watsonTags.";
  protected static final ObjectMapper SERIAILIZER = new ObjectMapper();

  protected static int getInteger(Object value) {
    return value == null ? 0 : (int) value;
  }

  protected static long getLong(Object value) {
    long views = 0L;
    if (value != null) {
      if (value instanceof Integer) {
        views = (long) (int) value;
      } else if (value instanceof Long) {
        views = (long) value;
      }

    }
    return views;
  }

  protected RestHighLevelClient getHighLevelClient() {
    return ElasticSearchRegistry.getRestHighLevelClient();
  }
  
  protected RestClient getLowLevelClient() {
    return ElasticSearchRegistry.getLowLevelRestClient();
  }

  protected String buildContentInfoIndexSrc(String id, String contentFormat, Map<String, Object> data){
    ContentInfoEio contentInfoEo = new ContentInfoEio();
    contentInfoEo.setId(id);
    contentInfoEo.setContentFormat(contentFormat);
    contentInfoEo.setIndexUpdatedTime(new Date(System.currentTimeMillis()));
    Map<String, Object> indexData = new HashMap<>();
    Map<String, Object> statistics = new HashMap<>();
    Map<String, Object> tags = new HashMap<>();
    Map<String, Object> watsonTags = new HashMap<>();
    boolean isStatistics = false;
    boolean isKeyword = false;
    if(data != null){
      for(String key : data.keySet()){
        if(key.startsWith(IndexerConstants.STATISTICS_DOT)) {
          isStatistics = true;
          statistics.put(key.replace(IndexerConstants.STATISTICS_DOT, ""), data.get(key));
        } else if (key.startsWith(IndexerConstants.INFO_WATSON_TAGS_DOT) && !((List<?>) data.get(key)).isEmpty()) {
          isKeyword = true;
          tags.put(key.replace(IndexerConstants.INFO_WATSON_TAGS_DOT, ""), data.get(key));
        }
      }
    }
    if (isStatistics) {
      indexData.put(IndexerConstants.STATISTICS, statistics);
      contentInfoEo.setStatistics(buildStatisticsData(contentFormat, indexData));
    } 
    if (isKeyword) {
      watsonTags.put(IndexerConstants.WATSON_TAGS, tags);
      contentInfoEo.setResourceInfo(new JsonObject(watsonTags));
    }
    LOGGER.debug("content info index source : " + contentInfoEo.getContentInfoJson().toString());
    return contentInfoEo.getContentInfoJson().toString();
  }

  @SuppressWarnings("unchecked")
  protected JsonObject buildStatisticsData(String contentFormat, Map<String, Object> contentInfoAsMap) {
    Map<String, Object> statisticsAsMap = null;
    JsonObject statistics = null;
    if (contentInfoAsMap != null) {
      statisticsAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.STATISTICS);
    }
    long viewsCount = 0L;
    int collaboratorCount = 0;
    int remixCount = 0;

    if (statisticsAsMap != null) {
      if (contentFormat != null && IndexerConstants.RESOURCE_FORMATS.matcher(contentFormat).matches()) {
        viewsCount = getLong(statisticsAsMap.get(ScoreConstants.VIEW_COUNT));
        StatisticsEo statisticEo = new StatisticsEo();
        statisticEo.setViewsCount(viewsCount);
        statistics = statisticEo.getStatistics();
      }
      if (contentFormat != null && IndexerConstants.COLLECTION_FORMATS.matcher(contentFormat).matches()) {
        viewsCount = getLong(statisticsAsMap.get(ScoreConstants.VIEW_COUNT));
        collaboratorCount = getInteger(statisticsAsMap.get(ScoreConstants.COLLAB_COUNT));
        remixCount = getInteger(statisticsAsMap.get(ScoreConstants.COLLECTION_REMIX_COUNT));
        StatisticsEo statisticEo = new StatisticsEo();
        statisticEo.setViewsCount(viewsCount);
        statisticEo.setCollaboratorCount(collaboratorCount);
        statisticEo.setCollectionRemixCount(remixCount);
        statistics = statisticEo.getStatistics();
      }
      if(contentFormat != null && contentFormat.equalsIgnoreCase(IndexerConstants.TYPE_COURSE)){
        CourseStatisticsEo courseStatistics = new CourseStatisticsEo();
        viewsCount = getLong(statisticsAsMap.get(ScoreConstants.VIEW_COUNT));
        collaboratorCount = getInteger(statisticsAsMap.get(ScoreConstants.COLLAB_COUNT));
        remixCount = getInteger(statisticsAsMap.get(IndexFields.COURSE_REMIXCOUNT));
        courseStatistics.setViewsCount(viewsCount);
        courseStatistics.setCourseRemixCount(remixCount);
        courseStatistics.setCollaboratorCount(collaboratorCount);
        statistics = courseStatistics;
      }
    }
    return statistics;
  }

  public String convertArrayToString(String inputString) {
    StringBuilder stringbuilder = new StringBuilder();
    if (inputString != null) {
      String[] stringArray = inputString.split(IndexerConstants.COMMA);
      for (String text : stringArray) {
        if (stringbuilder.length() > 0) {
          stringbuilder.append(IndexerConstants.COMMA);
        }
        stringbuilder = stringbuilder.append("\"" + text + "\"");
      }
    }
    return stringbuilder.toString();
  }
  
  public String convertArrayToLWAndString(String inputString) {
    StringBuilder stringbuilder = new StringBuilder();
    if (inputString != null) {
      String[] stringArray = inputString.split(IndexerConstants.COMMA);
      for (String text : stringArray) {
        if (stringbuilder.length() > 0) {
          stringbuilder.append(IndexerConstants.COMMA);
        }
        stringbuilder = stringbuilder.append("\"" + text.toLowerCase() + "\"");
      }
    }
    return stringbuilder.toString();
  }
  
  protected Response performRequest(String method, String indexUrl, String requestPayload) throws Exception {
    StringEntity entity = null;
    if (!StringUtil.isNullOrEmpty(requestPayload)) {
      entity = new StringEntity(requestPayload, ContentType.APPLICATION_JSON);
    }
    Response response = getLowLevelClient().performRequest(method, indexUrl, Collections.emptyMap(), entity);
    return response;
  }
  
}
