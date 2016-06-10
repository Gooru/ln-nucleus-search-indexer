package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;


@Table("course")
@IdName("unit_id")
public class Unit extends Model {
  public static final String GET_UNIT_COUNT = "SELECT count(*) from unit where course_id = ?::uuid and is_deleted = ?";
  
}
