package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface ContentRepository {

	  static ContentRepository instance(){
		  return new ContentRepositoryImpl();
	  }
	  
	  JsonObject getResource(String contentID);
	  JsonObject getDeletedResource(String contentID);
	  
	  JsonObject getQuestion(String contentID);
	  JsonObject getDeletedQuestion(String contentID);
	  
}
