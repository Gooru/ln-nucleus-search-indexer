package org.gooru.nuclues.search.indexers.app.repositories;

import io.vertx.core.json.JsonObject;

public interface ContentRepository {

	  JsonObject getResource(String contentID);
	  JsonObject getDeletedResource(String contentID);
	  
	  JsonObject getQuestion(String contentID);
	  JsonObject getDeletedQuestion(String contentID);
	  
}
