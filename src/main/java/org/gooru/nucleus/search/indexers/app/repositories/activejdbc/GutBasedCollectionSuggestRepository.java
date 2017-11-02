package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface GutBasedCollectionSuggestRepository {

  static GutBasedCollectionSuggestRepository instance() {
    return new GutBasedCollectionSuggestRepositoryImpl();
  }
  
  Boolean hasSuggestion(String codeId);

  void saveSuggestions(String id, JsonObject data);

}
