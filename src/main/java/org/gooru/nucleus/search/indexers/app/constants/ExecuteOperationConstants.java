package org.gooru.nucleus.search.indexers.app.constants;

public final class ExecuteOperationConstants {

  public static final String GET_RESOURCE = "get_resource";

  public static final String GET_QUESTION = "get_question";

  public static final String GET_CONTENT_BY_TYPE = "get_content_by_type";

  public static final String GET_COLLECTION = "get_collection";

  public static final String GET_COLLECTION_BY_TYPE = "get_collection_by_type";

  public static final String GET_COLLECTION_IDS = "get_collection_ids";

  public static final String GET_COLLECTION_QUESTION_PARENT_CONTENT_IDS = "get_collection_question_ids_and_content_parent_ids";

  public static final String GET_DELETED_RESOURCE = "get_deleted_resource";
  
  public static final String GET_DELETED_COLLECTION = "get_deleted_collection";
  
  public static final String GET_USER_RESOURCES = "get_user_resources";
  
  public static final String GET_USER_COLLECTIONS = "get_user_collections";


  private ExecuteOperationConstants() {
    throw new AssertionError();
  }
}
