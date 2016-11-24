package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.utils.InternalHelper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class KeywordEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler resourceIndexHandler;
  private final IndexHandler collectionIndexHandler;
  private final IndexHandler courseIndexHandler;

  public KeywordEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.resourceIndexHandler = getResourceIndexHandler();
    this.collectionIndexHandler = getCollectionIndexHandler();
    this.courseIndexHandler = getCourseIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("KEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
      // Parsing out Watson tags

      switch (eventName) {
      case EventsConstants.RESOURCE_UPDATE:
        LOGGER.debug("Event name is: " + eventName + ". Now updating the resource index...");
        handleResourceUpdate();
        break;

      case EventsConstants.RESOURCE_UPDATE_ALT:
        LOGGER.debug("Event name is: " + eventName + ". Now updating the resource index...");
        handleResourceAltUpdate();
        break;

      case EventsConstants.COURSE_UPDATE:
        LOGGER.debug("Event name is: " + eventName + ". Now updating the course index...");
        handleCourseUpdate();
        break;

      case EventsConstants.COURSE_UPDATE_ALT:
        LOGGER.info("Event name is: " + eventName + ". Now updating the course index...");
        handleCourseAltUpdate();
        break;

      case EventsConstants.COLLECTION_UPDATE:
        LOGGER.debug("Event name is: " + eventName + ". Now updating the collection index...");
        handleCollectionUpdate();
        break;

      case EventsConstants.COLLECTION_UPDATE_ALT:

        LOGGER.debug("Event name is: " + eventName + ". Now updating the collection index...");
        handleCollectionAltUpdate();
        break;

      default:
        LOGGER.error("KEH->handleEvents : Invalid event !! event name : " + eventName);
        throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      INDEX_FAILURES_LOGGER.error("Re-index failed for content. Event name : " + eventName + " Event json : "
              + (eventJson == null ? eventJson : eventJson.toString()) + " Exception :" + ex);
    }
  }

  private void handleCollectionAltUpdate() throws Exception {
    JsonArray enhancedData = eventJson.getJsonArray(EventsConstants.EVT_ENHANCED_METADATA);
    JsonArray watsonTags = InternalHelper.parseWatsonTags(enhancedData);
    Map<String, Object> sourceAsMap = generateSource(watsonTags);
    collectionIndexHandler.indexEnhancedKeywords(eventJson.getString(EventsConstants.EVT_DATA_ID), sourceAsMap);
  }

  private void handleCollectionUpdate() throws Exception {
    JsonArray enhancedData =
            eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_DATA).getJsonArray("enhanced_metadata");
    JsonArray watsonTags = InternalHelper.parseWatsonTags(enhancedData);
    Map<String, Object> sourceAsMap = generateSource(watsonTags);
    collectionIndexHandler.indexEnhancedKeywords(eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID), sourceAsMap);

  }

  private void handleCourseAltUpdate() throws Exception {
    JsonArray enhancedData = eventJson.getJsonArray(EventsConstants.EVT_ENHANCED_METADATA);
    // LOGGER.info("enhancedData: " + enhancedData.toString());
    JsonArray watsonTags = InternalHelper.parseWatsonTags(enhancedData);
    // LOGGER.info("Watson tags are: " + watsonTags);
    Map<String, Object> sourceAsMap = generateSource(watsonTags);
    courseIndexHandler.indexEnhancedKeywords(eventJson.getString(EventsConstants.EVT_DATA_ID), sourceAsMap);
  }

  private void handleCourseUpdate() throws Exception {
    JsonArray enhancedData =
            eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_DATA).getJsonArray("enhanced_metadata");
    JsonArray watsonTags = InternalHelper.parseWatsonTags(enhancedData);
    Map<String, Object> sourceAsMap = generateSource(watsonTags);
    courseIndexHandler.indexEnhancedKeywords(eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID), sourceAsMap);
  }

  private void handleResourceAltUpdate() throws Exception {
    JsonArray enhancedData = eventJson.getJsonArray(EventsConstants.EVT_ENHANCED_METADATA);
    JsonArray watsonTags = InternalHelper.parseWatsonTags(enhancedData);
    Map<String, Object> sourceAsMap = generateSource(watsonTags);
    resourceIndexHandler.indexEnhancedKeywords(eventJson.getString(EventsConstants.EVT_DATA_ID), sourceAsMap);
  }

  private void handleResourceUpdate() throws Exception {
    JsonArray enhancedData =
            eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_DATA).getJsonArray("enhanced_metadata");
    JsonArray watsonTags = InternalHelper.parseWatsonTags(enhancedData);
    Map<String, Object> sourceAsMap = generateSource(watsonTags);
    resourceIndexHandler.indexEnhancedKeywords(eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID), sourceAsMap);
  }
  
  private Map<String, Object> generateSource(JsonArray eventJson) {
    Map<String, Object> sourceAsMap = new HashMap<>();
    sourceAsMap.put("taxonomies", InternalHelper.getAppropriateTags(eventJson, "taxonomies"));
    sourceAsMap.put("concepts", InternalHelper.getAppropriateTags(eventJson, "concepts"));
    sourceAsMap.put("entities", InternalHelper.getAppropriateTags(eventJson, "entities"));
    sourceAsMap.put("keywords", InternalHelper.getAppropriateTags(eventJson, "keywords"));
    return sourceAsMap;
  }

}
