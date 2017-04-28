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
@Table(SchemaConstants.COLLECTION)
@IdName(SchemaConstants.ID)
public class Collection extends Model {
  public static final String COLLECTION = "collection";

  public static final String ASSESSEMENT = "assessment";
  
  public static final String GET_COLLECTION = "id = ?::uuid and is_deleted = ?";

  public static final String COLLECTION_QUERY = "format = ?::content_container_type and id = ?::uuid and is_deleted = ?";

  public static final String FETCH_RESOURCE_META = "select * from content where collection_id = ?::uuid and is_deleted = ?";

  public static final String FETCH_DELETED_QUERY = "id = ?::uuid and is_deleted = ?";
  
  public static final String FETCH_USER_COLLECTIONS = "(creator_id = ?::uuid or original_creator_id = ?::uuid or owner_id = ?::uuid) and is_deleted = ?";
  
  public static final String IS_DELETED = "is_deleted";
  
  public static final String GET_COLLECTION_COUNT_BY_COURSE = "course_id = ?::uuid and is_deleted = ?";

  public static final String GET_COLLECTION_COUNT_BY_UNIT = "unit_id = ?::uuid and is_deleted = ?";

  public static final String GET_COLLECTION_COUNT_BY_LESSON = "lesson_id = ?::uuid and is_deleted = ?";
  
  public static final String GET_USED_IN_COURSE_COUNT = "select count(distinct course_id) from collection where parent_collection_id = ?::uuid";

  public static final String GET_STUDENTS_OF_COLLECTION = "select count(cm.user_id) from class c inner join class_member cm on cm.class_id = c.id where course_id in (select distinct course_id from collection where parent_collection_id = ?::uuid) ";
  
}
