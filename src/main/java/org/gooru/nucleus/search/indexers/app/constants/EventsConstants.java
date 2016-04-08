package org.gooru.nucleus.search.indexers.app.constants;

public final class EventsConstants {

  // Event payload field names
  public static final String EVT_PAYLOAD_OBJECT = "payLoadObject";
  public static final String EVT_CONTEXT_OBJECT = "context";
  public static final String EVT_OBJECT_EVENT_NAME = "eventName";
  public static final String EVT_PAYLOAD_CONTENT_FORMAT = "contentFormat";
  public static final String EVT_PAYLOAD_SOURCE = "source";
  public static final String EVT_PAYLOAD_TARGET = "target";
  public static final String EVT_PAYLOAD_PARENT_CONTENT_ID = "parentContentId";
  public static final String EVT_PAYLOAD_ORIGINAL_CONTENT_ID = "originalContentId";
  public static final String EVT_PAYLOAD_PARENT_GOORU_ID = "parentGooruId";
  public static final String EVT_PAYLOAD_CONTENT_GOORU_ID = "contentGooruId";
  public static final String EVT_PAYLOAD_COLLABORATORS = "collaborators";
  public static final String EVT_REF_PARENT_GOORU_IDS = "referenceParentGooruIds";


  public static final String ITEM_CREATE = "item.create";
  public static final String ITEM_UPDATE = "item.update";
  public static final String ITEM_DELETE = "item.delete";
  public static final String ITEM_COPY = "item.copy";
  public static final String ITEM_MOVE = "item.move";
  public static final String COLLABORATORS_UPDATE = "collaborators.update";
  public static final String ITEM_ADD = "item.add";
  public static final String EVT_USER_CREATE = "event.user.create";
  public static final String EVT_USER_UPDATE = "user.update";

  private EventsConstants() {
    throw new AssertionError();
  }
}


