package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@DbName(IndexerConstants.DEFAULT_DATABASE_NAME)
@Table("unit")
@IdName("unit_id")
public class Unit extends Model {
  public static final String GET_UNIT_COUNT = "course_id = ?::uuid and is_deleted = ?";
  
}
