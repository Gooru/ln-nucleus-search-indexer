package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import io.vertx.core.json.JsonObject;

public class QuestionEventsHandler implements IndexEventHandler {

	private final JsonObject eventJson;

	public QuestionEventsHandler(JsonObject eventJson) {
		this.eventJson = eventJson;
	}

	@Override
	public void handleEvents() {
		// TODO Auto-generated method stub

	}

}
