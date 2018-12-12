package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface SignatureResourcesRepository {

  static SignatureResourcesRepository instance() {
    return new SignatureResourcesRepositoryImpl();
  }
  
  Boolean hasCuratedSuggestion(String codeId);

  void saveSuggestions(String id, JsonObject data);

  void deleteSuggestions();

  JsonArray getSignatureResourcesByGutCode(String gutCode);

  Boolean isCuratedSignatureResourceByItemId(String itemId);

}
