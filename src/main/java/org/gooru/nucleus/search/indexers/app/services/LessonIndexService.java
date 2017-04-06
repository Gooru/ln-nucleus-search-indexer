package org.gooru.nucleus.search.indexers.app.services;

import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface LessonIndexService {

  static LessonIndexService instance(){
    return new LessonIndexServiceImpl();
  }

  void setExistingStatisticsData(JsonObject result, Map<String, Object> contentInfoAsMap);
  
}
