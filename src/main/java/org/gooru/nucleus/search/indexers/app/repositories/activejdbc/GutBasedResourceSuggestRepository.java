package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface GutBasedResourceSuggestRepository {

  static GutBasedResourceSuggestRepository instance() {
    return new GutBasedResourceSuggestRepositoryImpl();
  }
  
  Boolean hasSuggestion(String codeId);

  void saveSuggestions(String id, JsonObject data);

}
