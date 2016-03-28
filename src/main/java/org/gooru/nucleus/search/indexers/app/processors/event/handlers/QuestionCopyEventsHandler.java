package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;

import io.vertx.core.json.JsonObject;

public class QuestionCopyEventsHandler extends BaseEventHandler implements IndexEventHandler {

	private final JsonObject eventJson;
    private String eventName;
	private String resourceId;
	private IndexHandler resourceIndexHandler;
	
	public QuestionCopyEventsHandler(JsonObject eventJson) {
		this.eventJson = eventJson;
		this.resourceIndexHandler = getResourceIndexHandler();
	}

	@Override
	public void handleEvents() {
		try{
			rejectIfInvalidEventJson(eventJson);
			eventName = eventJson.getString(EventsConstants.EVT_PAYLOAD_EVENT_NAME);
			LOGGER.debug("CCEH->handleEvents : Event validation passed, proceding to handle consumed event : " +eventName);
			resourceId = eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENTID);
			resourceIndexHandler.indexDocument(resourceId);
		}
		catch(Exception ex){
			LOGGER.error("CCEH->handleEvents : Index failed !! event name : " + eventName +" Event data received : " +(eventJson == null ? eventJson : eventJson.toString()) + " Exception : " +ex);
			INDEX_FAILURES_LOGGER.error("Re-index failed for resource. Event name : " + eventName +" Event json : " + (eventJson == null ? eventJson : eventJson.toString()) + " Exception :" +ex);
		}
	}
	

}
