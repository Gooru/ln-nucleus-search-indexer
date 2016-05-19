package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.javalite.activejdbc.Model;

public class Course extends Model {

  public static final String GET_COURSE_QUERY = "SELECT * from course where id = ?::uuid";

  public static final String IS_DELETED = "is_deleted";

}
