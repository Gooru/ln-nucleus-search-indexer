package org.gooru.nucleus.search.indexers.app.constants;

public final class ErrorMsgConstants {
  public static final String COLLECTION_DATA_NULL = "Collection data is null, db returned null value !!";
  public static final String COLLECTION_IDS_NULL = "Collection ids cannot be null on indexDocuments(ids)";
  public static final String RESOURCE_IDS_NULL = "Resoource ids cannot be null on indexDocuments(ids)";
  public static final String ORIGINAL_RESOURCE_DATA_NULL = "Original Resoure data is null, db returned null value !!";
  public static final String QUESTION_DATA_NULL = "Question data is null, db returned null value !!";
  public static final String RESOURCE_NOT_MAPPED = "Resoure not mapped with course !!";
  public static final String INVALID_EVENT_JSON = "JSON payload object is null or empty, Invalid event JSON !!";
  public static final String INVALID_COPY_EVENT_JSON = "JSON target/source object is null or empty, Invalid copy or move event JSON !!";
  public static final String INVALID_CONTENT_ID = "Content id is null or empty!, Invalid event JSON ";
  public static final String INVALID_COLLABORATORS_EVENT_JSON = "JSON collaborator info is null or empty, Invalid collaborator update event JSON !!";
  public static final String INVALID_ITEM_ADD_EVENT_JSON = "JSON item add target object is null or empty, Invalid item.add event JSON !!";
  public static final String RESOURCE_NOT_DELETED = "Given resource exists in DB !!, Delete from index failed"; 
  public static final String QUESTION_NOT_DELETED = "Given question exists in DB !!, Delete from index failed";
  public static final String RESOURCE_REF_NOT_DELETED = "Given resource ref exists in DB !!, Delete from index failed";
  public static final String COLLECTION_NOT_DELETED = "Given collection exists in DB !!, Delete from index failed"; 
  public static final String COURSE_DATA_NULL = "Course data is null, db returned null value !!";
  public static final String COURSE_NOT_DELETED = "Given course exists in DB !!, Delete from index failed"; 
  public static final String RUBRIC_DATA_NULL = "Rubric data is null, db returned null value !!";
  public static final String RUBRIC_NOT_DELETED = "Given rubric exists in DB !!, Delete from index failed"; 
  public static final String UNIT_DATA_NULL = "Unit data is null, db returned null value !!";
  public static final String UNIT_NOT_DELETED = "Given unit exists in DB !!, Delete from index failed"; 
  public static final String LESSON_DATA_NULL = "Lesson data is null, db returned null value !!";
  public static final String LESSON_NOT_DELETED = "Given lesson exists in DB !!, Delete from index failed"; 
  public static final String CROSSWALK_UNAVAILABLE = "Given crosswalk exist in DB !!, Delete from index failed"; 
  public static final String TAXONOMY_UNAVAILABLE = "Given taxonomy code exist in DB !!, Delete from index failed"; 
  public static final String RUBRIC_UNAVAILABLE = "Given rubric exist in DB !!, Delete from index failed"; 
  public static final String GUT_UNAVAILABLE = "Given gut code exist in DB !!, Delete from index failed"; 

  private ErrorMsgConstants() {
    throw new AssertionError();
  }
}
