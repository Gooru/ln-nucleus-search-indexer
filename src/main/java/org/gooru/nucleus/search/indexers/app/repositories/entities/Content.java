package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@Table("content")
@IdName("id")
public class Content extends Model {
  public static final String CONTENT_FORMAT_RESOURCE = "resource";
  public static final String CONTENT_FORMAT_QUESTION = "question";
  public static final String FETCH_CONTENT_QUERY = "content_format = ?::content_format_type and id = ?::uuid and is_deleted = ?";
  public static final String FETCH_DELETED_QUERY = "id = ?::uuid and is_deleted = ?";
  public static final String FETCH_USER_RESOURCES = "creator_id = ?::uuid or original_creator_id = ?::uuid and is_deleted = ?";
  public static final String FETCH_METADATA = "select * from metadata_reference where id = any(string_to_array(?,',')::integer[]);";
  public static final String FETCH_COLLECTION_META =
    "select collection.id, collection.title from content content inner join collection collection on collection.id = content.collection_id where " +
      "content.parent_content_id = ?::uuid;";
  public static final String FETCH_CONTENT =
    "select content.*, collection.id as collectionId, collection.title as collectionTitle from content content inner join collection collection on " +
      "collection.id = content.collection_id where content.content_format = ?::content_format_type and content.id = ?::uuid and content.is_deleted " +
      "= ?;";
  public static final String FETCH_QUESTION_AND_PARENT_CONTENT_IDS =
    "select parent_content_id, id, content_format from content where collection_id = ?::uuid";
  public static final String CONTENT_FORMAT = "content_format";

}
