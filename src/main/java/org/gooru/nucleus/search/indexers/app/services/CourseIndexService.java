package org.gooru.nucleus.search.indexers.app.services;

import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface CourseIndexService {

  static CourseIndexService instance(){
    return new CourseIndexServiceImpl();
  }
  
  public void indexDocument(String id, JsonObject data) throws Exception;
  
  public void deleteDocument(String id) throws Exception;

  void deleteIndexedCourse(String key, String type) throws Exception;

  void setExistingStatisticsData(JsonObject source, Map<String, Object> contentInfoAsMap);
  
}
