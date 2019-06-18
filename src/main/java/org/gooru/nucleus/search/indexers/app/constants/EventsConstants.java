package org.gooru.nucleus.search.indexers.app.constants;

import java.util.regex.Pattern;

public final class EventsConstants {

  // Event payload field names
  public static final String EVT_PAYLOAD_OBJECT = "payLoadObject";
  public static final String EVT_PAYLOAD_OBJECT_DATA = "data";
  public static final String EVT_CONTEXT_OBJECT = "context";
  public static final String EVT_OBJECT_EVENT_NAME = "eventName";
  public static final String EVT_PAYLOAD_CONTENT_FORMAT = "contentFormat";
  public static final String EVT_PAYLOAD_SOURCE = "source";
  public static final String EVT_PAYLOAD_TARGET = "target";
  public static final String EVT_PAYLOAD_PARENT_CONTENT_ID = "parentContentId";
  public static final String EVT_PAYLOAD_ORIGINAL_CONTENT_ID = "originalContentId";
  public static final String EVT_PAYLOAD_PARENT_GOORU_ID = "parentGooruId";
  public static final String EVT_PAYLOAD_CONTENT_GOORU_ID = "contentGooruId";
  public static final String EVT_PAYLOAD_COURSE_GOORU_ID = "courseGooruId";
  public static final String EVT_PAYLOAD_UNIT_GOORU_ID = "unitGooruId";
  public static final String EVT_PAYLOAD_LESSON_GOORU_ID = "lessonGooruId";
  public static final String EVT_PAYLOAD_COLLABORATORS = "collaborators";
  public static final String EVT_REF_PARENT_GOORU_IDS = "referenceParentGooruIds";
  public static final String EVT_PAYLOAD_USER_ID = "id";
  public static final String EVT_PAYLOAD_OBJECT_DATA_COLLECTION_ID = "collection_id";
  public static final String EVT_PAYLOAD_OBJECT_DATA_COURSE_ID = "course_id";
  public static final String EVT_PAYLOAD_OBJECT_DATA_UNIT_ID = "unit_id";
  public static final String EVT_PAYLOAD_OBJECT_DATA_LESSON_ID = "lesson_id";
  public static final String EVT_PAYLOAD_OBJECT_DATA_ORIGINAL_CONTENT_ID = "original_content_id";

  public static final String ITEM_CREATE = "item.create";
  public static final String ITEM_UPDATE = "item.update";
  public static final String ITEM_DELETE = "item.delete";
  public static final String ITEM_COPY = "item.copy";
  public static final String ITEM_MOVE = "item.move";
  public static final String COLLABORATORS_UPDATE = "collaborators.update";
  public static final String ITEM_ADD = "item.add";
  public static final String EVT_USER_CREATE = "event.user.create";
  public static final String EVT_USER_UPDATE = "event.user.update";
  
  // Events(pushed by insights) constants   
  public static final String EVT_UPDATE_VIEWS_COUNT = "views.update";
  public static final String EVT_DATA = "data";
  public static final String EVT_DATA_ID = "id";
  public static final String EVT_DATA_TYPE = "type";
  public static final String EVT_DATA_VIEW_COUNT = "viewsCount";
  public static final String EVT_PAYLOAD_COLLECTION_GOORU_ID = "collectionGooruId";
  
  // Events pushed by keyword manager
  public static final Pattern EVT_OF_KEYWORD_MANAGER = Pattern.compile("resourceIndexUpdate|resourceIndexUpdateAlt|courseIndexUpdate|courseIndexUpdateAlt|collectionIndexUpdate|collectionIndexUpdateAlt"); 
  public static final String RESOURCE_UPDATE = "resourceIndexUpdate";
  public static final String RESOURCE_UPDATE_ALT = "resourceIndexUpdateAlt";
  public static final String COURSE_UPDATE = "courseIndexUpdate";
  public static final String COURSE_UPDATE_ALT = "courseIndexUpdateAlt";
  public static final String COLLECTION_UPDATE = "collectionIndexUpdate";
  public static final String COLLECTION_UPDATE_ALT = "collectionIndexUpdateAlt";
  public static final String EVT_ENHANCED_METADATA = "enhanced_metadata";
  
  private EventsConstants() {
    throw new AssertionError();
  }
}


