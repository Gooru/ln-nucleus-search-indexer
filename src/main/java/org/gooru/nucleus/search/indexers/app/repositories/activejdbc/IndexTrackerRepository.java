package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;

import io.vertx.core.json.JsonObject;

public interface IndexTrackerRepository {

  static IndexTrackerRepository instance() {
    return new IndexTrackerRepositoryImpl();
  }
  void saveDeletedResource(String id, JsonObject data, List<String> attributes);
  void saveDeletedCollection(String id, JsonObject request, List<String> insertCollectionAllowedFields);
}
