package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface ConceptBasedResourceSuggestRepository {

  static ConceptBasedResourceSuggestRepository instance() {
    return new ConceptBasedResourceSuggestRepositoryImpl();
  }
  
  Boolean hasSuggestion(String codeId);

  void saveResourceSuggest(String id, JsonObject data);

}
