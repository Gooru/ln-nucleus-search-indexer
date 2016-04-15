package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;

import io.vertx.core.json.JsonObject;

public class UserEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler resourceIndexHandler;
  private final IndexHandler collectionIndexHandler;

  public UserEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.resourceIndexHandler = getResourceIndexHandler();
    this.collectionIndexHandler = getCollectionIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("UEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
      String userId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID);

      switch (eventName) {

      case EventsConstants.EVT_USER_CREATE:
        handleUserCreate(userId);
        break;
      case EventsConstants.EVT_USER_UPDATE:
        handleUserUpdate(userId);

      default:
        LOGGER.error("UEH->handleEvents : Invalid event !! event name : " + eventName);
        throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      LOGGER.error("UEH->handleEvents : Index failed !! event name : " + eventName + " Event data received : "
              + (eventJson == null ? eventJson : eventJson.toString()) + " Exception : " + ex);
      INDEX_FAILURES_LOGGER.error("Re-index failed for user. Event name : " + eventName + " Event json : "
              + (eventJson == null ? eventJson : eventJson.toString()) + " Exception :" + ex);
    }
  }
  
  private void handleUserCreate(String userId){
    
  }
  
  private void handleUserUpdate(String userId){
    
  }

}
