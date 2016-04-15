package org.gooru.nucleus.search.indexers.app.constants;

public final class ErrorMsgConstants {
  public static final String COLLECTION_DATA_NULL = "Collection data is null, db returned null value !!";
  public static final String COLLECTION_IDS_NULL = "Collection ids cannot be null on indexDocuments(ids)";
  public static final String RESOURCE_IDS_NULL = "Resoource ids cannot be null on indexDocuments(ids)";
  public static final String RESOURCE_DATA_NULL = "Resoure data is null, db returned null value !!";
  public static final String INVALID_EVENT_JSON = "JSON payload object is null or empty, Invalid event JSON !!";
  public static final String INVALID_COPY_EVENT_JSON = "JSON target/source object is null or empty, Invalid copy or move event JSON !!";
  public static final String INVALID_CONTENT_ID = "Content id is null or empty!, Invalid event JSON ";
  public static final String INVALID_COLLABORATORS_EVENT_JSON = "JSON collaborator info is null or empty, Invalid collaborator update event JSON !!";
  public static final String INVALID_ITEM_ADD_EVENT_JSON = "JSON item add target object is null or empty, Invalid item.add event JSON !!";
  public static final String RESOURCE_NOT_DELETED = "Given resource exists in DB !!, Delete from index failed"; 
  public static final String COLLECTION_NOT_DELETED = "Given collection exists in DB !!, Delete from index failed"; 


  private ErrorMsgConstants() {
    throw new AssertionError();
  }
}
