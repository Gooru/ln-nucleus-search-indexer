package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface IndexerRepo {
	
	public JsonObject getIndexDataContent();
	
	public JsonObject getIndexDataCollection();
	
	public JsonObject getAssessment();

}
