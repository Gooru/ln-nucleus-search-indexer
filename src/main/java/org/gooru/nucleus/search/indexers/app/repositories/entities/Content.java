package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.CONTENT)
@IdName(SchemaConstants.ID)
public class Content extends Model {
  public static final String CONTENT_FORMAT_RESOURCE = "resource";
  public static final String CONTENT_FORMAT_QUESTION = "question";
  public static final String FETCH_CONTENT_QUERY = "content_format = ?::content_format_type and id = ?::uuid and is_deleted = ?";
  public static final String FETCH_DELETED_QUERY = "id = ?::uuid and is_deleted = ?";
  public static final String FETCH_METADATA = "select * from metadata_reference where id = any(string_to_array(?,',')::integer[]);";
  public static final String FETCH_LICENSE_METADATA = "select * from metadata_reference where id = ?";
  public static final String FETCH_COLLECTION_META =
    "select collection.id, collection.title, collection.format from content content inner join collection collection on content.collection_id = collection.id where " +
      "content.parent_content_id = ?::uuid and content.is_deleted = false and collection.is_deleted = false;";
  public static final String FETCH_CONTENT =
    "select content.*, collection.id as collectionId, collection.title as collectionTitle from content content inner join collection collection on " +
      "collection.id = content.collection_id where content.content_format = ?::content_format_type and content.id = ?::uuid and content.is_deleted " +
      "= ?;";
  public static final String FETCH_QUESTION_AND_ORIGINAL_RESOURCE_IDS = "collection_id = ?::uuid";
  public static final String CONTENT_FORMAT = "content_format";
  public static final String IS_DELETED = "is_deleted";
  public static final String FETCH_USER_QUESTIONS = "content_format = ?::content_format_type and (creator_id = ?::uuid or original_creator_id = ?::uuid) and is_deleted = ?";
  public static final String FETCH_TWENTY_ONE_CENTURY_SKILL = "select * from twenty_one_century_skill where id = any(string_to_array(?,',')::integer[]);";

}
