package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface CollectionRepository {
	  JsonObject getCollection(String contentID);
	  JsonObject getDeletedCollection(String contentID);
	  
	  JsonObject getAssessment(String contentID);
	  JsonObject getDeletedAssessment(String contentID);

}
