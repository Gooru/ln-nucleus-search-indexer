package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface ContentVectorRepository {
  
  static ContentVectorRepository instance() {
    return new ContentVectorRepositoryImpl();
  }

  JsonObject getContentVectorsByContentId(String contentId, String contentType);

}
