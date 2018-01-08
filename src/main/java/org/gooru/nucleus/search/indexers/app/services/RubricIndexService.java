package org.gooru.nucleus.search.indexers.app.services;

import io.vertx.core.json.JsonObject;

public interface RubricIndexService {

  static RubricIndexService instance(){
    return new RubricIndexServiceImpl();
  }
  
  void indexDocument(String id, JsonObject data) throws Exception;

  void deleteDocument(String id) throws Exception;

  void deleteIndexedRubric(String key, String type) throws Exception;

}
