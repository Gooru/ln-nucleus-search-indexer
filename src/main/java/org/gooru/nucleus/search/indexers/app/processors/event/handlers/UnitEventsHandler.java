package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import java.util.Iterator;

import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.DeleteService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UnitEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler courseIndexHandler;
  private final IndexHandler unitIndexHandler;
  private final IndexHandler resourceIndexHandler;

  public UnitEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.courseIndexHandler = getCourseIndexHandler();    
    this.unitIndexHandler = getUnitIndexHandler();
    this.resourceIndexHandler = getResourceIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("UnitEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
      String unitId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID);

      switch (eventName) {

        case EventsConstants.ITEM_CREATE:
        case EventsConstants.ITEM_UPDATE:
        handleReIndex(unitId);
          break;
        case EventsConstants.ITEM_DELETE:
        deleteUnit(unitId);
          break;

        default:
          LOGGER.error("UnitEH->handleEvents : Invalid event !! event name : " + eventName);
          throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      LOGGER.error("UnitEH->handleEvents : Index failed !! event name : " + eventName + " Event data received : " +
        (eventJson == null ? eventJson : eventJson.toString()) + " Exception : " + ex);
      INDEX_FAILURES_LOGGER
        .error("Re-index failed for unit. Event name : " + eventName + " Event json : " + (eventJson == null ? eventJson : eventJson.toString()) +
          " Exception :" + ex);
    }
  }

  private void handleReIndex(String unitId) throws Exception {
    String courseId = eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT_DATA).getString(EventsConstants.EVT_PAYLOAD_OBJECT_DATA_COURSE_ID);
    unitIndexHandler.indexDocument(unitId);
    courseIndexHandler.indexDocument(courseId);
    LOGGER.debug("UEH->handleReIndex : Indexed unit! event name : " + eventName + " unit id : " + unitId);
  }  

  private void deleteUnit(String unitId) throws Exception {
    long start = System.currentTimeMillis();
    String courseId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_COURSE_GOORU_ID);
    unitIndexHandler.deleteIndexedDocument(unitId);
    courseIndexHandler.indexDocument(courseId);
    handlePostDelete(unitId);
    LOGGER.info("UEH-> Time taken to delete unit : {} : {} ms", unitId, (System.currentTimeMillis() - start));
  }
  
  private void handlePostDelete(String unitId) {
    try {
      LOGGER.debug("UEH->handlePostDelete : Proceding to index resources that had mapped to the unit : " + unitId);
      // Delete containing lessons
      JsonObject lessons = getDeletedLessonIdsOfUnit(unitId);
      JsonArray lessonIds = lessons.getJsonArray(IndexerConstants.LESSON_IDS);
      if (lessonIds != null && lessonIds.size() > 0) {
        DeleteService.instance().bulkDeleteDocuments(lessonIds,
          IndexerConstants.TYPE_LESSON, IndexNameHolder.getIndexName(EsIndex.LESSON));
        LOGGER.debug("UEH->handlePostDelete : Deleted lessons of unit id : " + unitId);
  
        // Delete containing items
        JsonObject items = getDeletedCollectionIdsOfUnit(unitId);
        JsonArray collectionIds = items.getJsonArray(IndexerConstants.COLLECTION_IDS);
        if (collectionIds != null && collectionIds.size() > 0) {
            DeleteService.instance().bulkDeleteDocuments(collectionIds,
              IndexerConstants.TYPE_COLLECTION, IndexNameHolder.getIndexName(EsIndex.COLLECTION));
            LOGGER.debug("UEH->handlePostDelete : Deleted containers of unit id : " + unitId);
            Iterator<Object> iter = collectionIds.iterator();
            while (iter.hasNext()) {
              String collectionId = (String) iter.next();
              // Delete containing contents
              handlePostCollectionDelete(collectionId);
            }
            LOGGER.debug("UEH->handlePostDelete : Deleted contents of containers of unit id : " + unitId);
        }
        
        // Delete containing rubrics
        JsonObject rubrics = getDeletedRubricIds(unitId);
        JsonArray rubricIds = rubrics.getJsonArray(IndexerConstants.RUBRIC_IDS);
        if (rubricIds != null && rubricIds.size() > 0) {
          DeleteService.instance().bulkDeleteDocuments(rubricIds,
            IndexerConstants.TYPE_RUBRIC, IndexNameHolder.getIndexName(EsIndex.RUBRIC));
          LOGGER.debug("UEH->handlePostDelete : Deleted rubrics of unit id : " + unitId);
        }
      }      
    } catch (Exception e) {
      LOGGER.error("UEH->handlePostDelete : Failed to delete associated contents that are mapped to this unit : " + unitId + "    Exception : " + e);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated contents that are mapped to this unit : " + unitId);
    }
  }
  
  private JsonObject getDeletedLessonIdsOfUnit(String unitId) {
    ProcessorContext context = new ProcessorContext(unitId, ExecuteOperationConstants.GET_DELETED_LESSON_IDS_OF_UNIT);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("UEH->getDeletedLessonIdsOfUnit : Fetched lesson data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  private JsonObject getDeletedCollectionIdsOfUnit(String unitId) {
    ProcessorContext context = new ProcessorContext(unitId, ExecuteOperationConstants.GET_DELETED_ITEM_IDS_OF_UNIT);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("UEH->getDeletedCollectionIdsOfUnit : Fetched item data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  public void handlePostCollectionDelete(String collectionId) {
    try {
      LOGGER.debug("UEH->handlePostCollectionDelete : Proceding to index resources that had mapped to the collection : " + collectionId);
      JsonObject contents = getCollectionQuestionIdsAndOriginalContentIds(collectionId);
      JsonArray questionIds = contents.getJsonArray(IndexerConstants.QUESTION_IDS);
      JsonArray resourceIds = contents.getJsonArray(IndexerConstants.ORIGINAL_CONTENT_IDS);
      JsonArray copiedResourceIds = contents.getJsonArray(IndexerConstants.RESOURCE_REFERENCES_IDS);
      
      if (questionIds != null && questionIds.size() > 0) {
        DeleteService.instance().bulkDeleteDocuments(questionIds,
          IndexerConstants.TYPE_RESOURCE, IndexNameHolder.getIndexName(EsIndex.RESOURCE));
        LOGGER.debug("UEH->handlePostCollectionDelete : Deleted questions inside collection id : " + collectionId);
      }

      if (copiedResourceIds != null && copiedResourceIds.size() > 0) {
        DeleteService.instance().bulkDeleteDocuments(copiedResourceIds,
          IndexerConstants.TYPE_RESOURCE, IndexNameHolder.getIndexName(EsIndex.RESOURCE));
        LOGGER.debug("UEH->handlePostCollectionDelete : Deleted copiedResourceId inside collection id : " + collectionId);
      }
      
      if (resourceIds != null && resourceIds.size() > 0) {
        Iterator<Object> iter = resourceIds.iterator();
        while (iter.hasNext()) {
          String resourceId = (String) iter.next();
          resourceIndexHandler.decreaseCount(resourceId, ScoreConstants.USED_IN_COLLECTION_COUNT);
          LOGGER.debug("UEH->handlePostCollectionDelete : Decreased used in collection count id : " + resourceId);
        }
      }
      
    } catch (Exception e) {
      LOGGER.error("UEH->handlePostCollectionDelete : Failed to re-index associated resources that is mapped to this collection : " + collectionId + "    Exception : " + e);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated resources that is mapped to this collection : " + collectionId);
    }
  }
  
  private JsonObject getCollectionQuestionIdsAndOriginalContentIds(String collectionId) {
    ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_COLLECTION_QUESTION_AND_ORIGINAL_RESOURCE_IDS);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("UEH->getCollectionResources : Fetched resource data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  private JsonObject getDeletedRubricIds(String unitId) {
    ProcessorContext context = new ProcessorContext(unitId, ExecuteOperationConstants.GET_DELETED_RUBRIC_IDS_OF_UNIT);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("UEH->getDeletedRubricIds : Fetched rubric data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }

}
