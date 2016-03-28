package org.gooru.nucleus.search.indexers.app.constants;

public class EventsConstants {

	  //Content related events
	  public static final String EVT_RES_CREATE = "event.resource.create";
	  public static final String EVT_RES_UPDATE = "event.resource.update";
	  public static final String EVT_RES_DELETE = "event.resource.delete";

	  public static final String EVT_QUESTION_CREATE = "event.question.create";
	  public static final String EVT_QUESTION_UPDATE = "event.question.update";
	  public static final String EVT_QUESTION_DELETE = "event.question.delete";

	  public static final String EVT_COLLECTION_CREATE = "event.collection.create";
	  public static final String EVT_COLLECTION_UPDATE = "event.collection.update";
	  public static final String EVT_COLLECTION_DELETE = "event.collection.delete";
      public static final String EVT_COLLECTION_CONTENT_ADD = "event.collection.content.add";
      
	  public static final String EVT_ASSESSMENT_CREATE = "event.assessment.create";
	  public static final String EVT_ASSESSMENT_UPDATE = "event.assessment.update";
	  public static final String EVT_ASSESSMENT_DELETE = "event.assessment.delete";
	  public static final String EVT_ASSESSMENT_QUESTION_ADD = "event.assessment.question.add";

	  public static final String EVT_COLLABORATOR_UPDATE_ASSESSMENT = "event.assessment.collaborator.update";
	  public static final String EVT_COLLABORATOR_UPDATE_COLLECTION = "event.collection.collaborator.update";
	  
	  public static final String EVT_COLLECTION_COPY = "event.collection.copy";
	  public static final String EVT_ASSESSMENT_COPY = "event.assessment.copy";
	  public static final String EVT_QUESTION_COPY = "event.question.copy";
	  public static final String EVT_LESSON_COPY = "event.lesson.copy";
	  public static final String EVT_UNIT_COPY = "event.unit.copy";
	  public static final String EVT_COURSE_COPY = "event.course.copy";

	  public static final String EVT_USER_CREATE = "event.user.create";
	  public static final String EVT_USER_UPDATE = "user.update";
	  
	  // Event payload field names
	  public static final String EVT_PAYLOAD_OBJECT = "payLoadObject";
	  public static final String EVT_PAYLOAD_CONTENTID = "contentId";
	  public static final String EVT_PAYLOAD_EVENT_NAME = "eventName";

}


