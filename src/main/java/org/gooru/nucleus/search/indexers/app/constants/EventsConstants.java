package org.gooru.nucleus.search.indexers.app.constants;

public class EventsConstants {

	  public static final String MSG_EVENT_NAME = "event.name";
	  public static final String MSG_EVENT_BODY = "event.body";

	  public static final String MSG_USER_ANONYMOUS = "anonymous";
	  public static final String MSG_USER_ID = "userId";
	  public static final String MSG_HEADER_TOKEN = "session.token";

	  public static final String MSG_EVENT_TIMESTAMP = "event.timestamp";
	  public static final String MSG_EVENT_DUMP = "event.dump";

	  // Operation names: Also need to be updated in corresponding handlers
	  //Content related events
	  public static final String EVT_RES_GET = "resource.get";
	  public static final String EVT_RES_CREATE = "resource.create";
	  public static final String EVT_RES_UPDATE = "resource.update";
	  public static final String EVT_RES_DELETE = "resource.delete";
	  public static final String EVT_RES_COPY = "resource.copy";

	  public static final String EVT_QUESTION_CREATE = "question.create";
	  public static final String EVT_QUESTION_UPDATE = "question.update";
	  public static final String EVT_QUESTION_DELETE = "question.delete";
	  public static final String EVT_QUESTION_COPY = "question.copy";

	  public static final String EVT_COLLECTION_CREATE = "collection.create";
	  public static final String EVT_COLLECTION_UPDATE = "collection.update";
	  public static final String EVT_COLLECTION_DELETE = "collection.delete";
	  public static final String EVT_COLLECTION_COPY = "collection.copy";

	  public static final String EVT_ASSESSMENT_CREATE = "assessment.create";
	  public static final String EVT_ASSESSMENT_UPDATE = "assessment.update";
	  public static final String EVT_ASSESSMENT_DELETE = "assessment.delete";
	  public static final String EVT_ASSESSMENT_COPY = "assessment.copy";

	  public static final String EVT_LESSON_CREATE = "lesson.create";
	  public static final String EVT_LESSON_UPDATE = "lesson.update";
	  public static final String EVT_LESSON_DELETE = "lesson.delete";
	  public static final String EVT_LESSON_COPY = "lesson.copy";

	  public static final String EVT_UNIT_CREATE = "unit.create";
	  public static final String EVT_UNIT_UPDATE = "unit.update";
	  public static final String EVT_UNIT_DELETE = "unit.delete";
	  public static final String EVT_UNIT_COPY = "unit.copy";

	  public static final String EVT_COURSE_CREATE = "course.create";
	  public static final String EVT_COURSE_UPDATE = "course.update";
	  public static final String EVT_COURSE_DELETE = "course.delete";
	  public static final String EVT_COURSE_COPY = "course.copy";

	  public static final String EVT_USER_CREATE = "user.create";
	  public static final String EVT_USER_UPDATE = "user.update";
	  public static final String EVT_USER_DELETE = "user.delete";
	  
	  public static final String EVT_PAYLOAD_OBJECT = "payLoadObject";
	  public static final String EVT_PAYLOAD_CONTENTID = "contentId";
	  

	  // Event Structure Type
	  public static final int EST_ERROR = -1;
	  public static final int EST_ITEM_CREATE = 0;
	  public static final int EST_ITEM_EDIT = 1;
	  public static final int EST_ITEM_COPY = 2;
	  public static final int EST_ITEM_MOVE = 3;
	  public static final int EST_ITEM_DELETE = 4;

}
