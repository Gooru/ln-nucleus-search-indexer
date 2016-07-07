package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonObject;

@SuppressWarnings("rawtypes")
public interface UserRepository {
  
  static UserRepository instance() {
    return new UserRepositoryImpl();
  }
  
  JsonObject getUser(String userId);

  List<Map> getUserDetails(String userID);
}
