package org.gooru.nucleus.search.indexers.app.services;

import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface UnitIndexService {

  static UnitIndexService instance(){
    return new UnitIndexServiceImpl();
  }

  void setExistingStatisticsData(JsonObject result, Map<String, Object> contentInfoAsMap);

  void deleteIndexedUnit(String key, String type) throws Exception;
  
}
