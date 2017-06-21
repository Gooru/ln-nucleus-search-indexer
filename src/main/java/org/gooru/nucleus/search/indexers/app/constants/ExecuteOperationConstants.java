package org.gooru.nucleus.search.indexers.app.constants;

public final class ExecuteOperationConstants {

  public static final String GET_RESOURCE = "get_resource";

  public static final String GET_QUESTION = "get_question";

  public static final String GET_CONTENT_BY_TYPE = "get_content_by_type";

  public static final String GET_COLLECTION = "get_collection";

  public static final String GET_COLLECTION_BY_TYPE = "get_collection_by_type";

  public static final String GET_COLLECTION_IDS = "get_collection_ids";

  public static final String GET_COLLECTION_QUESTION_AND_ORIGINAL_RESOURCE_IDS = "get_collection_question_ids_and_content_parent_ids";

  public static final String GET_DELETED_RESOURCE = "get_deleted_resource";
  
  public static final String GET_DELETED_COLLECTION = "get_deleted_collection";
  
  public static final String GET_USER_ORIGINAL_RESOURCES = "get_user_original_resources";
  
  public static final String GET_USER_COLLECTIONS = "get_user_collections";
  
  public static final String GET_COURSE = "get_course";
  
  public static final String SAVE_DELETED_RESOURCE = "save_deleted_resource";

  public static final String GET_DELETED_COURSE = "get_deleted_course";
  
  public static final String SAVE_DELETED_COLLECTION = "save_deleted_collection";

  public static final String GET_USER_QUESTIONS = "get_user_questions";

  public static final String GET_DELETED_QUESTION = "get_deleted_question";

  public static final String GET_GDT_MAPPING = "get_gdt_mapping";

  public static final String GET_UNIT = "get_unit";

  public static final String GET_LESSON = "get_lesson";

  public static final String GET_RUBRIC = "get_rubric";

  public static final String GET_DELETED_RUBRIC = "get_deleted_rubric";
  
  public static final String GET_TAXONOMY_CODE = "get_taxonomy_code";
  
  public static final String GET_CROSSWALK = "get_crosswalk";

  private ExecuteOperationConstants() {
    throw new AssertionError();
  }
}
