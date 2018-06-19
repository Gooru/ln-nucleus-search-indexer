package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface LibraryRepository {

    static LibraryRepository instance() {
      return new LibraryRepositoryImpl();
    }

    JsonObject getLibraryContentById(String contentId);
    
}
