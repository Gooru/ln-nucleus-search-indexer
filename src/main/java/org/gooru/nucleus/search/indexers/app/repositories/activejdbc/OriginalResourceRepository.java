package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface OriginalResourceRepository {

  static OriginalResourceRepository instance() {
    return new OriginalResourceRepositoryImpl();
  }

  JsonObject getResource(String contentID);
  
  JsonObject getDeletedContent(String contentId);
  
  JsonObject getUserOriginalResources(String userId);
  
}
