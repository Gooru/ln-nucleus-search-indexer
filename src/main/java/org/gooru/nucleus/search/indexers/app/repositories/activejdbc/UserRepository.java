package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface UserRepository {
  
  static UserRepository instance() {
    return new UserRepositoryImpl();
  }
  
  JsonObject getUser(String userID);

}
