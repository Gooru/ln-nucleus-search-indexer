package org.gooru.nucleus.search.indexers.app.repositories.entities;

public class Taxonomy {

  public static final String GET_SUBJECT_QUERY = "SELECT * from default_subject where id = ?";
  
  public static final String GET_COURSE_QUERY = "SELECT * from default_course where id = ?";

  public static final String GET_DOMAIN_QUERY = "SELECT * from default_domain where id = ?";

  public static final String GET_CODE = "SELECT * from default_code where id = ?";

}
