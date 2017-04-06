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
@Table(SchemaConstants.COURSE)
@IdName(SchemaConstants.ID)
public class Course extends Model {

  public static final String GET_COURSE_QUERY = "SELECT * from course where id = ?::uuid";

  public static final String FETCH_DELETED_QUERY = "id = ?::uuid and is_deleted = ?";

  public static final String IS_DELETED = "is_deleted";

}
