package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author Renuka
 */

@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.LESSON)
@IdName(SchemaConstants.LESSON_ID)
public class Lesson extends Model {
  
  public static final String GET_LESSON_BY_UNIT_ID = "unit_id = ?::uuid and is_deleted = ?";

  public static final String GET_LESSON_QUERY = "lesson_id = ?::uuid and is_deleted = ?";

  public static final String FETCH_DELETED_QUERY = "lesson_id = ?::uuid and is_deleted = ?";

  public static final String IS_DELETED = "is_deleted";
  
  public static final String GET_LESSON_BY_COURSE_ID = "course_id = ?::uuid and is_deleted = ?";

}
