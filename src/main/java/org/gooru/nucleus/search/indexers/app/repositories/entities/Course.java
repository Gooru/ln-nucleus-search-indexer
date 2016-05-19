package org.gooru.nucleus.search.indexers.app.repositories.entities;

public class Course {

  public static final String GET_COURSE_QUERY = "SELECT * from course where id = ?::uuid";

}
