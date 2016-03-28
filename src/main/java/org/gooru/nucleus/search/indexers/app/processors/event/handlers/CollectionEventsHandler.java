package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;

import io.vertx.core.json.JsonObject;

public class CollectionEventsHandler extends BaseEventHandler implements IndexEventHandler {

	private final JsonObject eventJson;
	private String eventName;
	private String collectionId;
    private IndexHandler collectionIndexHandler;
    
	public CollectionEventsHandler(JsonObject eventJson) {
		this.eventJson = eventJson;
		this.collectionIndexHandler = getCollectionIndexHandler();
	}

	@Override
	public void handleEvents() {
		try{
			rejectIfInvalidEventJson(eventJson);
			eventName = eventJson.getString(EventsConstants.EVT_PAYLOAD_EVENT_NAME);
			LOGGER.debug("CEH->handleEvents : Event validation passed, proceding to handle consumed event : " +eventName);
			collectionId = eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENTID);
			
			if(eventName.equalsIgnoreCase(EventsConstants.EVT_COLLECTION_DELETE) || eventName.equalsIgnoreCase(EventsConstants.EVT_ASSESSMENT_DELETE)){
				collectionIndexHandler.deleteIndexedDocument(collectionId);
				// Need to decide if we need to delete private questions from index after collection/assessment deleted. 
			}
			else {
				collectionIndexHandler.indexDocument(collectionId);
			    LOGGER.debug("CEH->handleEvents : Indexed collection! event name : " + eventName + "collection id : " + collectionId);
			}
		}
		catch(Exception ex){
			LOGGER.error("CEH->handleEvents : Index failed !! event name : " + eventName +" Event data received : " +eventJson.toString()+ " Exception : " +ex);
			INDEX_FAILURES_LOGGER.error("Re-index failed for collection. Event name : " + eventName +" Event json : " + eventJson.toString() + " Exception :" +ex);
		}
	}
}
