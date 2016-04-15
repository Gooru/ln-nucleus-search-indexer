package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class StatisticsEventsHandler implements IndexEventHandler{


  public static final Logger LOGGER = LoggerFactory.getLogger(IndexEventHandler.class);
  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  
  private final JsonObject eventJson;
  private String eventName;

  public StatisticsEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("UEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);

      switch (eventName) {

      case EventsConstants.EVT_UPDATE_VIEWS_COUNT:
        updateViewCountBulk();
        break;

      default:
        LOGGER.error("SEH->handleEvents : Invalid event !! event name : " + eventName);
        throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      LOGGER.error("SEH->handleEvents : Index failed !! event name : " + eventName + " Event data received : "
              + (eventJson == null ? eventJson : eventJson.toString()) + " Exception : " + ex);
    }
  }

  private void updateViewCountBulk() {
    JsonArray data = eventJson.getJsonArray(EventsConstants.EVT_DATA);
    if(data != null && data.size() > 0){
      IndexService.instance().bulkIndexStatisticsField(data);
    }
    else {
      throw new InvalidRequestException("Invalid data in view count update event, not able to handle");
    }
  }

}
