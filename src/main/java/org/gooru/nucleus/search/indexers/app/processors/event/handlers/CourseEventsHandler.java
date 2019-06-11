package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class CourseEventsHandler extends BaseEventHandler implements IndexEventHandler {

  private final JsonObject eventJson;
  private String eventName;
  private final IndexHandler courseIndexHandler;

  public CourseEventsHandler(JsonObject eventJson) {
    this.eventJson = eventJson;
    this.courseIndexHandler = getCourseIndexHandler();
  }

  @Override
  public void handleEvents() {
    try {
      eventName = eventJson.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      LOGGER.debug("CEH->handleEvents : Event validation passed, proceding to handle consumed event : " + eventName);
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
  
  //TODO delete associated collections and questions which has is_deleted=true 
  private void deleteCourse(String courseId) throws Exception {
    courseIndexHandler.deleteIndexedDocument(courseId);
    LOGGER.debug("CREH->handleDelete : Deleted course! event name : " + eventName + " course id : " + courseId);
  }
  
  //TODO Need to clarify - remix count of original course / parent course should be incremented
  //TODO Discussion already done and confirmation required - copies of collections and resources are not indexed here considering index overload and lots of duplicates in search.
  private void handleCopyCourse(String courseId) throws Exception {
    String parentCourseId = getOriginalContentIdTargetObj(eventJson);
    courseIndexHandler.indexDocument(courseId);
    courseIndexHandler.increaseCount(parentCourseId, IndexFields.COURSE_REMIXCOUNT);
    LOGGER.debug("CREH->handleCopy : Indexed course! event name : " + eventName + " course id : " + courseId);
  }
  
  private void handleUpdateCollaborators(String collectionId) throws Exception {
    try {
      ValidationUtil.rejectIfInvalidJsonCollaboratorUpdate(eventJson);
      courseIndexHandler.indexDocument(collectionId);
    } catch (Exception e) {
      LOGGER.error("Failed to update collaborator count for course : " + collectionId);
      throw new Exception(e);
    }
  }

}
