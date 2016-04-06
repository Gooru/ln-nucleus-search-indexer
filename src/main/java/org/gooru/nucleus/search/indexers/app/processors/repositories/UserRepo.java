package org.gooru.nucleus.search.indexers.app.processors.repositories;

import io.vertx.core.json.JsonObject;


public interface UserRepo {
  JsonObject getUser(String userID);

  JsonObject getDeletedUser(String userID);
}
