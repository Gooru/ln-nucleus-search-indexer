package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.UNIT)
@IdName(SchemaConstants.UNIT_ID)
public class Unit extends Model {
  
  public static final String GET_UNIT_COUNT = "course_id = ?::uuid and is_deleted = ?";

  public static final String GET_UNIT_QUERY = "unit_id = ?::uuid and is_deleted = ?";

  public static final String FETCH_DELETED_QUERY = "unit_id = ?::uuid and is_deleted = ?";

  public static final String IS_DELETED = "is_deleted";
  
  public static final String GET_UNITS_OF_COURSE = "course_id = ?::uuid and is_deleted = ?";

}
