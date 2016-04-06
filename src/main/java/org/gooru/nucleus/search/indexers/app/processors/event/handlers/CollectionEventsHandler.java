package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.constants.*;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import java.util.Iterator;

public class CollectionEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler collectionIndexHandler;
  private final IndexHandler resourceIndexHandler;

  public CollectionEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.collectionIndexHandler = getCollectionIndexHandler();
    this.resourceIndexHandler = getResourceIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("CEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
      String collectionId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_CONTEXT_CONTENT_ID);

      switch (eventName) {

        case EventsConstants.ITEM_CREATE:
        case EventsConstants.ITEM_UPDATE:
          handleReIndex(collectionId);
          break;

        case EventsConstants.ITEM_DELETE:
          handleDelete(collectionId);
          break;

        case EventsConstants.ITEM_COPY:
          handleCopy(collectionId);
          break;

        case EventsConstants.COLLABORATORS_UPDATE:
          handleUpdateCollaborators(collectionId);
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


  private void handleReIndex(String collectionId) throws Exception {
    collectionIndexHandler.indexDocument(collectionId);
    LOGGER.debug("CEH->handleReIndex : Indexed collection! event name : " + eventName + " collection id : " + collectionId);
  }

  private void handleDelete(String collectionId) throws Exception {
    collectionIndexHandler.deleteIndexedDocument(collectionId);
    LOGGER.debug("REH->handleDelete : Deleted collection from index! event name : " + eventName + " collection id : " + collectionId);
    handlePostDelete(collectionId);
  }

  private void handlePostDelete(String collectionId) {
    try {
      LOGGER.debug("CEH->handlePostDelete : Proceding to index resources that had mapped to the collection : " + collectionId);
      // Index resources inside copied collection
      JsonObject ids = getCollectionQuestionIdsAndParentIds(collectionId);
      JsonArray questionIds = ids.getJsonArray(IndexerConstants.QUESTION_IDS);
      JsonArray resourceIds = ids.getJsonArray(IndexerConstants.PARENT_CONTENT_IDS);

      if (questionIds != null && questionIds.size() > 0) {
        Iterator<Object> iter = questionIds.iterator();
        while (iter.hasNext()) {
          resourceIndexHandler.deleteIndexedDocument((String) iter.next());
          LOGGER.debug("CEH->handleCopy : Deleted questions inside collection id : " + collectionId + " question id : " + iter.next());
        }
      }

      if (resourceIds != null && resourceIds.size() > 0) {
        Iterator<Object> iter = resourceIds.iterator();
        while (iter.hasNext()) {
          resourceIndexHandler.decreaseCount(ScoreConstants.USED_IN_COLLECTION_COUNT, (String) iter.next());
          LOGGER.debug("CEH->handleCopy : Decreased used in collection count id : " + iter.next());
        }
      }
    } catch (Exception e) {
      LOGGER.error("CEH->handlePostDelete : Failed to re-index associated resources that is mapped to this collection : " + collectionId);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated resources that is mapped to this collection : " + collectionId);
    }
  }

  private JsonObject getCollectionQuestionIdsAndParentIds(String collectionId) {
    ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_COLLECTION_QUESTION_PARENT_CONTENT_IDS);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CEH->getCollectionResources : Fetched resource data from DB, json : " + result.toString() + " Calling index service !!");
    ValidationUtil.rejectIfNull(result, ErrorMsgConstants.RESOURCE_IDS_NULL);
    return result;
  }

  private void handleCopy(String collectionId) throws Exception {
    try {
      ValidationUtil.rejectIfInvalidJsonCopyEvent(eventJson);
      LOGGER.debug("CEH->handleCopy : copy events validation passed, info - target object " + getPayLoadTargetObj(eventJson).toString());
      collectionIndexHandler.indexDocument(collectionId);
      String originalContentId = getOriginalContentIdTargetObj(eventJson);
      collectionIndexHandler.increaseCount(originalContentId, ScoreConstants.COLLECTION_REMIX_COUNT);
      // Index resources inside copied collection
      JsonObject ids = getCollectionQuestionIdsAndParentIds(collectionId);
      JsonArray questionIds = ids.getJsonArray(IndexerConstants.QUESTION_IDS);
      JsonArray resourceIds = ids.getJsonArray(IndexerConstants.PARENT_CONTENT_IDS);

      if (questionIds != null && questionIds.size() > 0) {
        Iterator<Object> iter = questionIds.iterator();
        while (iter.hasNext()) {
          resourceIndexHandler.indexDocument((String) iter.next());
          LOGGER.debug("CEH->handleCopy : Re-indexed question id : " + iter.next());
        }
      }

      if (resourceIds != null && resourceIds.size() > 0) {
        Iterator<Object> iter = resourceIds.iterator();
        while (iter.hasNext()) {
          resourceIndexHandler.increaseCount(ScoreConstants.USED_IN_COLLECTION_COUNT, (String) iter.next());
          LOGGER.debug("CEH->handleCopy : Incremented used in collection count id : " + iter.next());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Failed to handle copy event for collection id : " + collectionId);
      throw new Exception(e);
    }
  }

  private void handleUpdateCollaborators(String collectionId) throws Exception {
    try {
      ValidationUtil.rejectIfInvalidJsonCollaboratorUpdate(eventJson);
      JsonArray collaborators = getCollaborators(eventJson);
      collectionIndexHandler.updateCount(collectionId, ScoreConstants.COLLAB_COUNT, collaborators.size());
    } catch (Exception e) {
      LOGGER.error("Failed to update collaborator count for collection : " + collectionId);
      throw new Exception(e);
    }
  }

}
