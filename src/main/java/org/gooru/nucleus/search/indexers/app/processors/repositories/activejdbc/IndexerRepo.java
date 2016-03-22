package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface IndexerRepo {
	
	public JsonObject getResoure();
	
	public JsonObject getCollection();
	
	public JsonObject getAssessment();

}
