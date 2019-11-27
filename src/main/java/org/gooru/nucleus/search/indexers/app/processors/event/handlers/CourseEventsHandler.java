package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import java.util.Iterator;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.DeleteService;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CourseEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler courseIndexHandler;
  private final IndexHandler resourceIndexHandler;

  public CourseEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.courseIndexHandler = getCourseIndexHandler();
    this.resourceIndexHandler = getResourceIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("CREH->handleEvents : Event validation passed, proceeding to handle consumed event : " + eventName);
      String courseId = eventJson.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_GOORU_ID);

      switch (eventName) {

        case EventsConstants.ITEM_CREATE:
        case EventsConstants.ITEM_UPDATE:
          handleReIndex(courseId);
          break;

        case EventsConstants.ITEM_DELETE:
          deleteCourse(courseId);
          break;

        case EventsConstants.ITEM_COPY:
          handleCopyCourse(courseId);
          break;
          
        case EventsConstants.COLLABORATORS_UPDATE:
          handleUpdateCollaborators(courseId);
          break;
          
        default:
          LOGGER.error("CREH->handleEvents : Invalid event !! event name : " + eventName);
          throw new InvalidRequestException("Invalid event, not able to handle");
      }
    } catch (Exception ex) {
      LOGGER.error("CREH->handleEvents : Index failed !! event name : " + eventName + " Event data received : " +
        (eventJson == null ? eventJson : eventJson.toString()) + " Exception : " + ex);
      INDEX_FAILURES_LOGGER
        .error("Re-index failed for course. Event name : " + eventName + " Event json : " + (eventJson == null ? eventJson : eventJson.toString()) +
          " Exception :" + ex);
    }
  }

  private void handleReIndex(String courseId) throws Exception {
    courseIndexHandler.indexDocument(courseId);
    LOGGER.debug("CREH->handleReIndex : Indexed course! event name : " + eventName + " course id : " + courseId);
  }
  
  private void deleteCourse(String courseId) throws Exception {
    courseIndexHandler.deleteIndexedDocument(courseId);
    handlePostDelete(courseId);
    LOGGER.debug("CREH->handleDelete : Deleted course! event name : " + eventName + " course id : " + courseId);
  }
  
  //TODO Need to clarify - remix count of original course / parent course should be incremented
  private void handleCopyCourse(String courseId) throws Exception {
    long start = System.currentTimeMillis();
    String parentCourseId = getOriginalContentIdTargetObj(eventJson);
    courseIndexHandler.indexDocument(courseId);
    courseIndexHandler.increaseCount(parentCourseId, IndexFields.COURSE_REMIXCOUNT);
    handlePostCopy(courseId);
    LOGGER.info("CREH-> Time taken to copy course : {} ms", (System.currentTimeMillis() - start));
    LOGGER.debug("CREH->handleCopy : Indexed course! event name : " + eventName + " course id : " + courseId);
  }
  
  private void handlePostCopy(String courseId) throws Exception {
    try {     
      LOGGER.debug("CREH->handleCopy : Proceeding to index contents mapped to the course : " + courseId);
      // Index containing units
      JsonObject units = getUnitsOfCourse(courseId);
      JsonArray unitObjs = units.getJsonArray("units");
      if (unitObjs != null && unitObjs.size() > 0) {
        IndexService.instance().bulkIndexDocuments(unitObjs,
          IndexerConstants.TYPE_UNIT, IndexNameHolder.getIndexName(EsIndex.UNIT));
        LOGGER.debug("CREH->handleCopy : Indexed units of course id : " + courseId);
        
        // Index containing lessons
        JsonObject lessons = getLessonsOfCourse(courseId);
        JsonArray lessonObjs = lessons.getJsonArray("lessons");
        if (lessonObjs != null && lessonObjs.size() > 0) {
          IndexService.instance().bulkIndexDocuments(lessonObjs,
            IndexerConstants.TYPE_LESSON, IndexNameHolder.getIndexName(EsIndex.LESSON));
          LOGGER.debug("CREH->handleCopy : Indexed lessons of course id : " + courseId);
          
          // Index containing items
          JsonObject items = getItemsOfCourse(courseId);
          JsonArray collectionObjs = items.getJsonArray(IndexerConstants.COLLECTIONS);
          if (collectionObjs != null && collectionObjs.size() > 0) {
            IndexService.instance().bulkIndexDocuments(collectionObjs,
                IndexerConstants.TYPE_COLLECTION, IndexNameHolder.getIndexName(EsIndex.COLLECTION));
              Iterator<Object> iter = collectionObjs.iterator();
              while (iter.hasNext()) {
                String jsonStr = (String) iter.next();
                JsonObject data = new JsonObject(jsonStr);
                if (data != null) {
                  String collectionId = data.getString(EntityAttributeConstants.ID);
                  // Index containing contents
                  handlePostCollectionCopyOrDelete(collectionId, "index");
                }
                LOGGER.debug("CREH->handleCopy : Indexed containing items of course id : " + courseId);
              }
              LOGGER.debug("CREH->handleCopy : Indexed containers of course id : " + courseId);
          }
          
          // Index containing rubrics
          JsonObject rubrics = getRubricsOfCourse(courseId);
          JsonArray rubricIds = rubrics.getJsonArray("rubrics");
          if (rubricIds != null && rubricIds.size() > 0) {
            IndexService.instance().bulkIndexDocuments(rubricIds,
              IndexerConstants.TYPE_RUBRIC, IndexNameHolder.getIndexName(EsIndex.RUBRIC));
            LOGGER.debug("CREH->handleCopy : Indexed rubrics inside course id : " + courseId);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Failed to handle copy event for collection id : " + courseId, e);
      throw new Exception(e);
    }
  }
  
  private JsonObject getUnitsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_UNITS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getUnitIdsOfCourse : Fetched unit data from DB, Calling index service !!");
    return result;
  }
  
  private JsonObject getLessonsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_LESSONS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getLessonIdsOfCourse : Fetched lesson data from DB, Calling index service !!");
    return result;
  }
  
  private JsonObject getItemsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_ITEMS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getCollectionIdsOfCourse : Fetched item data from DB, Calling index service !!");
    return result;
  }
  
  private JsonObject getRubricsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_RUBRICS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getRubricIds : Fetched rubric data from DB, Calling index service !!");
    return result;
  }
  
  private void handleUpdateCollaborators(String courseId) throws Exception {
    try {
      ValidationUtil.rejectIfInvalidJsonCollaboratorUpdate(eventJson);
      courseIndexHandler.indexDocument(courseId);
      // Index containing items
      JsonObject items = getItemsOfCourse(courseId);
      JsonArray collectionIds = items.getJsonArray(IndexerConstants.COLLECTIONS);
      if (collectionIds != null && collectionIds.size() > 0) {
        IndexService.instance().bulkIndexDocuments(collectionIds,
            IndexerConstants.TYPE_COLLECTION, IndexNameHolder.getIndexName(EsIndex.COLLECTION));
          LOGGER.debug("CREH->handleUpdateCollaborators : Indexed containers of course id : " + courseId);
      }
    } catch (Exception e) {
      LOGGER.error("Failed to update collaborator for course : " + courseId);
      throw new Exception(e);
    }
  }
  
  private void handlePostDelete(String courseId) {
    try {
      LOGGER.debug("CREH->handlePostDelete : Proceding to index resources that had mapped to the course : " + courseId);
      // Delete containing units
      JsonObject units = getDeletedUnitIdsOfCourse(courseId);
      JsonArray unitIds = units.getJsonArray(IndexerConstants.UNIT_IDS);
      if (unitIds != null && unitIds.size() > 0) {
        DeleteService.instance().bulkDeleteDocuments(unitIds,
          IndexerConstants.TYPE_UNIT, IndexNameHolder.getIndexName(EsIndex.UNIT));
        LOGGER.debug("CREH->handlePostDelete : Deleted units of course id : " + courseId);
        
        // Delete containing lessons
        JsonObject lessons = getDeletedLessonIdsOfCourse(courseId);
        JsonArray lessonIds = lessons.getJsonArray(IndexerConstants.LESSON_IDS);
        if (lessonIds != null && lessonIds.size() > 0) {
          DeleteService.instance().bulkDeleteDocuments(lessonIds,
            IndexerConstants.TYPE_LESSON, IndexNameHolder.getIndexName(EsIndex.LESSON));
          LOGGER.debug("CREH->handlePostDelete : Deleted lessons of course id : " + courseId);
          
          // Delete containing items
          JsonObject items = getDeletedCollectionIdsOfCourse(courseId);
          JsonArray collectionIds = items.getJsonArray(IndexerConstants.COLLECTION_IDS);
          if (collectionIds != null && collectionIds.size() > 0) {
              DeleteService.instance().bulkDeleteDocuments(collectionIds,
                IndexerConstants.TYPE_COLLECTION, IndexNameHolder.getIndexName(EsIndex.COLLECTION));
              LOGGER.debug("CREH->handlePostDelete : Deleted containers of course id : " + courseId);
              Iterator<Object> iter = collectionIds.iterator();
              while (iter.hasNext()) {
                String collectionId = (String) iter.next();
                // Delete containing contents
                handlePostCollectionCopyOrDelete(collectionId, "delete");
              }
              LOGGER.debug("CREH->handlePostDelete : Deleted containing contents of course id : " + courseId);
          }
          
          // Delete containing rubrics
          JsonObject rubrics = getDeletedRubricIdsOfCourse(courseId);
          JsonArray rubricIds = rubrics.getJsonArray(IndexerConstants.RUBRIC_IDS);
          if (rubricIds != null && rubricIds.size() > 0) {
            DeleteService.instance().bulkDeleteDocuments(rubricIds,
              IndexerConstants.TYPE_RUBRIC, IndexNameHolder.getIndexName(EsIndex.RUBRIC));
            LOGGER.debug("CREH->handlePostDelete : Deleted rubrics inside course id : " + courseId);
          }
        }
      }
      
    } catch (Exception e) {
      LOGGER.error("CREH->handlePostDelete : Failed to delete associated contents that are mapped to this course : " + courseId + "    Exception : " + e);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated contents that are mapped to this course : " + courseId);
    }
  }
  
  private JsonObject getDeletedUnitIdsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_DELETED_UNIT_IDS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getDeletedUnitIdsOfCourse : Fetched unit data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  private JsonObject getDeletedLessonIdsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_DELETED_LESSON_IDS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getDeletedLessonIdsOfCourse : Fetched lesson data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  private JsonObject getDeletedCollectionIdsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_DELETED_ITEM_IDS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getDeletedCollectionIdsOfCourse : Fetched item data from DB, json : " + result.toString() + " Calling index service !!");
    return result;
  }
  
  public void handlePostCollectionCopyOrDelete(String collectionId, String indexType) {
    try {
      LOGGER.debug("CREH->handlePostCollectionCopyOrDelete : Proceding to index resources that had mapped to the collection : " + collectionId);
      JsonObject contents = getCollectionQuestionIdsAndOriginalContentIds(collectionId);
      JsonArray questionIds = contents.getJsonArray(IndexerConstants.QUESTION_IDS);
      JsonArray origResIds = contents.getJsonArray(IndexerConstants.ORIGINAL_CONTENT_IDS);
      JsonArray copiedResourceIds = contents.getJsonArray(IndexerConstants.RESOURCE_REFERENCES_IDS);
      
      if (indexType.equalsIgnoreCase("delete")) {
        deleteContents(collectionId, questionIds);
        deleteContents(collectionId, copiedResourceIds);
      } else if (indexType.equalsIgnoreCase("index")) {
        JsonObject collContents = getCollectionContentIds(collectionId);
        JsonArray copiedContents = collContents.getJsonArray("contents");
        indexContents(collectionId, copiedContents);
      }
      incrOrDecrUsedInCollCountBasedOnIndexRequest(indexType, origResIds);

    } catch (Exception e) {
      LOGGER.error("CREH->handlePostCollectionCopyOrDelete : Failed to re-index associated resources that is mapped to this collection : " + collectionId + "    Exception : " + e);
      INDEX_FAILURES_LOGGER.error("Failed to re-index associated resources that is mapped to this collection : " + collectionId);
    }
  }

  private void indexContents(String collectionId, JsonArray contentIds) throws Exception {
    if (contentIds != null && contentIds.size() > 0) {
      IndexService.instance().bulkIndexDocuments(contentIds, IndexerConstants.TYPE_RESOURCE_REFERENCE,
        IndexNameHolder.getIndexName(EsIndex.RESOURCE));
      LOGGER.debug("CREH->handlePostCollectionCopyOrDelete : Indexed contents inside collection id : " + collectionId);
    }
  }
  
  private void deleteContents(String collectionId, JsonArray contentIds) throws Exception {
    if (contentIds != null && contentIds.size() > 0) {
      DeleteService.instance().bulkDeleteDocuments(contentIds, IndexerConstants.TYPE_RESOURCE,
        IndexNameHolder.getIndexName(EsIndex.RESOURCE));
      LOGGER.debug("CREH->handlePostCollectionCopyOrDelete : Deleted contents inside collection id : " + collectionId);
    }
  }

  private void incrOrDecrUsedInCollCountBasedOnIndexRequest(String indexType, JsonArray origResIds) throws Exception {
    if (origResIds != null && origResIds.size() > 0) {
      Iterator<Object> iter = origResIds.iterator();
      while (iter.hasNext()) {
        String resourceId = (String) iter.next();
        if (indexType.equalsIgnoreCase("delete")) {
          resourceIndexHandler.decreaseCount(resourceId, ScoreConstants.USED_IN_COLLECTION_COUNT);
          LOGGER.debug("CREH->handlePostCollectionCopyOrDelete : Decreased used in collection count id : " + resourceId);
        } else if (indexType.equalsIgnoreCase("index")) {
          resourceIndexHandler.increaseCount(resourceId, ScoreConstants.USED_IN_COLLECTION_COUNT);
          LOGGER.debug("CREH->handlePostCollectionCopyOrDelete : Increased used in collection count id : " + resourceId);
        }
      }
    }
  }
  
  private JsonObject getCollectionQuestionIdsAndOriginalContentIds(String collectionId) {
    ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_COLLECTION_QUESTION_AND_ORIGINAL_RESOURCE_IDS);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getCollectionResources : Fetched resource data from DB, Calling index service !!");
    return result;
  }
  
  private JsonObject getCollectionContentIds(String collectionId) {
    ProcessorContext context = new ProcessorContext(collectionId, ExecuteOperationConstants.GET_CONTENTS_OF_COLLECTION);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getCollectionResources : Fetched resource data from DB, Calling index service !!");
    return result;
  }
  
  private JsonObject getDeletedRubricIdsOfCourse(String courseId) {
    ProcessorContext context = new ProcessorContext(courseId, ExecuteOperationConstants.GET_DELETED_RUBRIC_IDS_OF_COURSE);
    JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    LOGGER.debug("CREH->getDeletedRubricIds : Fetched rubric data from DB, Calling index service !!");
    return result;
  }

}
