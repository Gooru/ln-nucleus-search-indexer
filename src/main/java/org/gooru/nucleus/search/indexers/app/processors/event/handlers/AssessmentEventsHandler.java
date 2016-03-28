package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import io.vertx.core.json.JsonObject;

public class AssessmentEventsHandler implements IndexEventHandler {
	
	private final JsonObject eventJson;
	
	public AssessmentEventsHandler(JsonObject eventJson) {
		this.eventJson = eventJson;
	}

	@Override
	public void handleEvents() {
		// TODO Auto-generated method stub

	}

}
