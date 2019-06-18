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

public class LessonEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler courseIndexHandler;
  private final IndexHandler unitIndexHandler;
  private final IndexHandler lessonIndexHandler;
  private final IndexHandler resourceIndexHandler;

  public LessonEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.courseIndexHandler = getCourseIndexHandler(); 
    this.unitIndexHandler = getUnitIndexHandler();
    this.lessonIndexHandler = getLessonIndexHandler();
    this.resourceIndexHandler = getResourceIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("LessonEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
      String lessonId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID);

      switch (eventName) {

        case EventsConstants.ITEM_CREATE:
        case EventsConstants.ITEM_UPDATE:
          handleReIndex(lessonId);
          break;
        case EventsConstants.ITEM_DELETE:
          deleteLesson(lessonId);
          break;

        default:
          LOGGER.error("LessonEH->handleEvents : Invalid event !! event name : " + eventName);
          throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      LOGGER.error("LessonEH->handleEvents : Index failed !! event name : " + eventName + " Event data received : " +
        (eventJson == null ? eventJson : eventJson.toString()) + " Exception : " + ex);
      INDEX_FAILURES_LOGGER
        .error("Re-index failed for lesson. Event name : " + eventName + " Event json : " + (eventJson == null ? eventJson : eventJson.toString()) +
          " Exception :" + ex);
    }
  }

  private void handleReIndex(String lessonId) throws Exception {
    String courseId = eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT_DATA).getString(EventsConstants.EVT_PAYLOAD_OBJECT_DATA_COURSE_ID);
    String unitId = eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT_DATA).getString(EventsConstants.EVT_PAYLOAD_OBJECT_DATA_UNIT_ID);
    lessonIndexHandler.indexDocument(lessonId);
    courseIndexHandler.indexDocument(courseId);
    unitIndexHandler.indexDocument(unitId);
    LOGGER.debug("LEH->handleReIndex : Indexed lesson! event name : " + eventName + " lesson id : " + lessonId);
  }

  private void deleteLesson(String lessonId) throws Exception {
    long start = System.currentTimeMillis();
    String courseId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_COURSE_GOORU_ID);
    String unitId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_UNIT_GOORU_ID);
    lessonIndexHandler.deleteIndexedDocument(lessonId);
    courseIndexHandler.indexDocument(courseId);
    unitIndexHandler.indexDocument(unitId);
    handlePostDelete(lessonId);
    LOGGER.info("LEH-> Time taken to delete lesson : {} : {} ms", lessonId, (System.currentTimeMillis() - start));
  }
  
  private void handlePostDelete(String lessonId) {
    try {
      LOGGER.debug("LEH->handlePostDelete : Proceding to index resources that had mapped to the unit : " + lessonId);
      // Delete containing items
      JsonObject items = getDeletedCollectionIdsOfLesson(lessonId);
      JsonArray collectionIds = items.getJsonArray(IndexerConstants.COLLECTION_IDS);
      if (collectionIds != null && collectionIds.size() > 0) {
          DeleteService.instance().bulkDeleteDocuments(collectionIds,
            IndexerConstants.TYPE_COLLECTION, IndexNameHolder.getIndexName(EsIndex.COLLECTION));
          LOGGER.debug("LEH->handlePostDelete : Deleted containers of unit id : " + lessonId);
          Iterator<Object> iter = collectionIds.iterator();
          while (iter.hasNext()) {
            String collectionId = (String) iter.next();
            // Delete containing contents
            handlePostCollectionDelete(collectionId);
          }
          LOGGER.debug("LEH->handlePostDelete : Deleted contents of containers of unit id : " + lessonId);
      }
      
      // Delete containing rubrics
      JsonObject rubrics = getDeletedRubricIds(lessonId);
      JsonArray rubricIds = rubrics.getJsonArray(IndexerConstants.RUBRIC_IDS);
      if (rubricIds != null && rubricIds.size() > 0) {
        DeleteService.instance().bulkDeleteDocuments(rubricIds,
          IndexerConstants.TYPE_RUBRIC, IndexNameHolder.getIndexName(EsIndex.RUBRIC));
        LOGGER.debug("LEH->handlePostDelete : Deleted rubrics inside lesson id : " + lessonId);
      }
    } catch (Exception e) {
      LOGGER.error("LEH->handlePostDelete : Failed to delete associated contents that are mapped to this unit : " + lessonId + "    Exception : " + e);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated contents that are mapped to this unit : " + lessonId);
    }
  }

  private JsonObject getDeletedCollectionIdsOfLesson(String lessonId) {
    ProcessorContext context = new ProcessorContext(lessonId, ExecuteOperationConstants.GET_DELETED_ITEM_IDS_OF_LESSON);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("LEH->getDeletedCollectionIdsOfLesson : Fetched item data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  public void handlePostCollectionDelete(String collectionId) {
    try {
      LOGGER.debug("LEH->handlePostCollectionDelete : Proceding to index resources that had mapped to the collection : " + collectionId);
      // Index resources inside copied collection
      JsonObject contents = getCollectionQuestionIdsAndOriginalContentIds(collectionId);
      JsonArray questionIds = contents.getJsonArray(IndexerConstants.QUESTION_IDS);
      JsonArray resourceIds = contents.getJsonArray(IndexerConstants.ORIGINAL_CONTENT_IDS);
      JsonArray copiedResourceIds = contents.getJsonArray(IndexerConstants.RESOURCE_REFERENCES_IDS);
      
      if (questionIds != null && questionIds.size() > 0) {
        DeleteService.instance().bulkDeleteDocuments(questionIds,
          IndexerConstants.TYPE_RESOURCE, IndexNameHolder.getIndexName(EsIndex.RESOURCE));
        LOGGER.debug("LEH->handlePostCollectionDelete : Deleted questions inside collection id : " + collectionId);
      }

      if (copiedResourceIds != null && copiedResourceIds.size() > 0) {
        DeleteService.instance().bulkDeleteDocuments(copiedResourceIds,
          IndexerConstants.TYPE_RESOURCE, IndexNameHolder.getIndexName(EsIndex.RESOURCE));
        LOGGER.debug("LEH->handlePostCollectionDelete : Deleted copiedResourceId inside collection id : " + collectionId);
      }
      
      if (resourceIds != null && resourceIds.size() > 0) {
        Iterator<Object> iter = resourceIds.iterator();
        while (iter.hasNext()) {
          String resourceId = (String) iter.next();
          resourceIndexHandler.decreaseCount(resourceId, ScoreConstants.USED_IN_COLLECTION_COUNT);
          LOGGER.debug("LEH->handlePostCollectionDelete : Decreased used in collection count id : " + resourceId);
        }
      }
      
    } catch (Exception e) {
      LOGGER.error("LEH->handlePostCollectionDelete : Failed to re-index associated contents that are mapped to this collection : " + collectionId + "    Exception : " + e);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated contents that are mapped to this collection : " + collectionId);
    }
  }
  
  private JsonObject getCollectionQuestionIdsAndOriginalContentIds(String collectionId) {
    ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_COLLECTION_QUESTION_AND_ORIGINAL_RESOURCE_IDS);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("LEH->getCollectionResources : Fetched resource data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  private JsonObject getDeletedRubricIds(String lessonId) {
    ProcessorContext context = new ProcessorContext(lessonId, ExecuteOperationConstants.GET_DELETED_RUBRIC_IDS_OF_LESSON);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("LEH->getDeletedRubricIds : Fetched rubric data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }

}
