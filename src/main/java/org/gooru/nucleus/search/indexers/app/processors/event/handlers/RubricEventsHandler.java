package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;

import io.vertx.core.json.JsonObject;

public class RubricEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler rubricIndexHandler;
  private final IndexHandler questionIndexHandler;

  public RubricEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.questionIndexHandler = getQuestionIndexHandler();
    this.rubricIndexHandler = getRubricIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("REH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
      String rubricId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID);

      switch (eventName) {

      case EventsConstants.ITEM_CREATE:
      case EventsConstants.ITEM_UPDATE:
        handleReIndex(rubricId);
        break;

      case EventsConstants.ITEM_DELETE:
        handleDelete(rubricId);
        break;

      default:
        LOGGER.error("RubEH->handleEvents : Invalid event !! event name : " + eventName);
        throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      INDEX_FAILURES_LOGGER.error("Re-index failed for rubric. Event name : " + eventName + " Event json : "
              + (eventJson == null ? eventJson : eventJson.toString()) + " Exception :" + ex);
    }
  }

  private void handleReIndex(String rubricId) throws Exception {
    rubricIndexHandler.indexDocument(rubricId);
    LOGGER.debug("RubEH->handleReIndex : Indexed rubric! event name : " + eventName + " rubric id : " + rubricId);
  }

  private void handleDelete(String rubricId) throws Exception {
    rubricIndexHandler.deleteIndexedDocument(rubricId);
    LOGGER.debug("RubEH->handleDelete : Deleted rubric from index! event name : " + eventName + " rubric id : " + rubricId);
  }

}
