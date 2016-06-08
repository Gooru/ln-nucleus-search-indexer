package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.ContentFormat;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ResourceEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler resourceIndexHandler;
  private final IndexHandler collectionIndexHandler;

  public ResourceEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.collectionIndexHandler = getCollectionIndexHandler();
    this.resourceIndexHandler = getResourceIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("REH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
      String resourceId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID);

      switch (eventName) {

        case EventsConstants.ITEM_CREATE:
          handleReIndex(resourceId);
          break;
          
        case EventsConstants.ITEM_UPDATE:
          handleItemUpdate(resourceId);
          break;

        case EventsConstants.ITEM_DELETE:
          handleDelete(resourceId);
          break;

        case EventsConstants.ITEM_COPY:
          handleCopy(resourceId);
          break;

        case EventsConstants.ITEM_MOVE:
          handleMove(resourceId);
          break;

        case EventsConstants.ITEM_ADD:
          handleResourceAdd(resourceId);
          break;

        default:
          LOGGER.error("REH->handleEvents : Invalid event !! event name : " + eventName);
          throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      LOGGER.error("REH->handleEvents : Index failed !! event name : " + eventName + " Event data received : " +
        (eventJson == null ? eventJson : eventJson.toString()) + " Exception : " + ex);
      INDEX_FAILURES_LOGGER
        .error("Re-index failed for resource. Event name : " + eventName + " Event json : " + (eventJson == null ? eventJson : eventJson.toString()) +
          " Exception :" + ex);
    }
  }

  

  private void handleItemUpdate(String resourceId) throws Exception {
    resourceIndexHandler.indexDocument(resourceId);
    LOGGER.debug("REH->handleReIndex : Indexed resource! event name : " + eventName + " resource id : " + resourceId);
    String collectionId = getCollectionId(eventJson);
    if(collectionId != null && !collectionId.isEmpty()){
      collectionIndexHandler.indexDocument(collectionId);
      LOGGER.debug("REH->handleReIndex : Indexed parent collection of resource: " + resourceId + " collection id  : " + collectionId);
    }
  }

  private void handleReIndex(String resourceId) throws Exception {
    resourceIndexHandler.indexDocument(resourceId);
    LOGGER.debug("REH->handleReIndex : Indexed resource! event name : " + eventName + " resource id : " + resourceId);
  }

  private void handleDelete(String resourceId) throws Exception {
    resourceIndexHandler.deleteIndexedDocument(resourceId);
    LOGGER.debug("REH->handleDelete : Deleted resource from index! event name : " + eventName + " resource id : " + resourceId);
    handlePostDelete(resourceId);
  }

  private void handlePostDelete(String resourceId) {
    try {
      LOGGER.debug("REH->handlePostDelete : Proceding to index collection/assessment that had mapped to resource : " + resourceId);
      
      // Decrease used in collection count of parent resource
      String parentContentId = getParentContentIdContextObj(eventJson);
      if(parentContentId != null ){
        resourceIndexHandler.indexDocument(parentContentId);
      }
      
      // Re-index all the collections deleted resource mapped with.
      JsonObject payload = getPayLoadObj(eventJson);
      JsonArray collectionIds = payload.getJsonArray(EventsConstants.EVT_REF_PARENT_GOORU_IDS);

      if (collectionIds == null || collectionIds.size() == 0) {
        LOGGER.debug("Zero collections mapped with this deleted resource id : " + resourceId);
        return;
      }

      JsonObject idsJson = new JsonObject();
      idsJson.put(IndexerConstants.COLLECTION_IDS, collectionIds);
      collectionIndexHandler.indexDocuments(idsJson);
    } catch (Exception ex) {
      LOGGER.error("REH->handlePostDelete : Failed to re-index associated colelctions/assessments that is mapped to this resource : " + resourceId);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated colelctions/assessments that is mapped to this resource : " + resourceId);
    }
  }

  private void handleCopy(String resourceId) throws Exception {
    try {
      ValidationUtil.rejectIfInvalidJsonCopyEvent(eventJson);
      String contentFormat = getPayLoadObjContentFormat(eventJson);
      String parentContentId = getParentContentIdTargetObj(eventJson);

      LOGGER.debug("REH->handleCopy : copy events validation passed, info - target object " + getPayLoadTargetObj(eventJson).toString());

      if (contentFormat.equalsIgnoreCase(ContentFormat.QUESTION.name())) {
        resourceIndexHandler.indexDocument(resourceId);
        // update used in collection count 
        resourceIndexHandler.indexDocument(parentContentId);
        LOGGER.debug("REH->handleCopy : Re-indexed question id : " + resourceId);
      } else if (contentFormat.equalsIgnoreCase(ContentFormat.RESOURCE.name())) {
        // update used in collection count
        resourceIndexHandler.indexDocument(parentContentId);
      }
    } catch (Exception e) {
      LOGGER.error("Failed to handle copy event for resource id : " + resourceId);
      throw new Exception(e);
    }
  }

  private void handleMove(String resourceId) throws Exception {
    try {
      ValidationUtil.rejectIfInvalidJsonMoveEvent(eventJson);
      JsonObject idsJson = new JsonObject();
      idsJson
        .put(IndexerConstants.COLLECTION_IDS, new JsonArray().add(getParentGooruIdTargetObj(eventJson)).add(getParentGooruIdSourceObj(eventJson)));
      resourceIndexHandler.indexDocument(resourceId);
      collectionIndexHandler.indexDocuments(idsJson);
    } catch (Exception e) {
      LOGGER.error("Failed to handle move event for resource id : " + resourceId);
      throw new Exception(e);
    }
  }

  private void handleResourceAdd(String resourceId) throws Exception {
    try {
      ValidationUtil.rejectIfInvalidJsonItemAddEvent(eventJson);
      String parentContentId = getParentContentIdTargetObj(eventJson);
      String contentFormat = getPayLoadObjContentFormat(eventJson);
      String parentGooruId = getParentGooruIdTargetObj(eventJson);

      collectionIndexHandler.indexDocument(parentGooruId);
      LOGGER.debug("Indexed parent collection/assesment on item.add  collection id : " + parentGooruId);

      if (contentFormat.equalsIgnoreCase(ContentFormat.QUESTION.name())) {
        resourceIndexHandler.indexDocument(resourceId);
        
        // update used in collection count
        if(parentContentId != null){
          resourceIndexHandler.indexDocument(parentContentId);
        }
        LOGGER.debug(
          "Indexed question on item.add  question id : " + resourceId + " Incremented used in collection count question id : " + parentContentId);
      } else {
        // update used in collection count
        resourceIndexHandler.indexDocument(parentContentId);
        LOGGER.debug("Incremented used in collection count on item.add  resource id : " + parentContentId);
      }
    } catch (Exception e) {
      LOGGER.error("Failed to handle resource add for resource id : " + resourceId);
      throw new Exception(e);
    }
  }

}
