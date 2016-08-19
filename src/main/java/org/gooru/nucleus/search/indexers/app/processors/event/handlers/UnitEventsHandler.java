package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;

import io.vertx.core.json.JsonObject;

public class UnitEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler courseIndexHandler;

  public UnitEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.courseIndexHandler = getCourseIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      String courseId = null;
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("UnitEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);

      switch (eventName) {

        case EventsConstants.ITEM_CREATE:
          courseId = eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT_DATA).getString(EventsConstants.EVT_PAYLOAD_OBJECT_DATA_COURSE_ID);
          courseIndexHandler.indexDocument(courseId);
          break;
        //TODO delete associated collections and questions which are not already deleted 
        case EventsConstants.ITEM_DELETE:
          courseId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_COURSE_GOORU_ID);
          courseIndexHandler.indexDocument(courseId);
          break;

        default:
          LOGGER.error("UnitEH->handleEvents : Invalid event !! event name : " + eventName);
          throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      LOGGER.error("UnitEH->handleEvents : Index failed !! event name : " + eventName + " Event data received : " +
        (eventJson == null ? eventJson : eventJson.toString()) + " Exception : " + ex);
      INDEX_FAILURES_LOGGER
        .error("Re-index failed for course. Event name : " + eventName + " Event json : " + (eventJson == null ? eventJson : eventJson.toString()) +
          " Exception :" + ex);
    }
  }

}
