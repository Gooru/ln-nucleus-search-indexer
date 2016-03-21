package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface UserRepository {
	  JsonObject getUser(String userID);
}
