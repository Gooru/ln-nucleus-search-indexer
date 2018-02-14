package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface SignatureItemsRepository {

  static SignatureItemsRepository instance() {
    return new SignatureItemsRepositoryImpl();
  }
  
  Boolean hasCuratedSuggestion(String codeId);

  void saveSuggestions(String id, JsonObject data);

  void deleteSuggestions(String itemFormat);

  JsonArray getSignatureItemsByGutCode(String gutCode, String contentType);

}
