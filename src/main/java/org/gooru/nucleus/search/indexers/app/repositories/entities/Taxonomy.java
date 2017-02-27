package org.gooru.nucleus.search.indexers.app.repositories.entities;

public class Taxonomy {

  public static final String GET_SUBJECT_QUERY = "SELECT * from taxonomy_subject where id = ?";
  
  public static final String GET_COURSE_QUERY = "SELECT * from taxonomy_course where id = ?";

  public static final String GET_DOMAIN_QUERY = "SELECT * from taxonomy_domain where id = ?";

  public static final String GET_CODE = "SELECT * from taxonomy_code where id = ?";

  public static final String GET_GDT_CODE = "SELECT source_taxonomy_code_id, target_display_code, target_framework_id from taxonomy_code_mapping where target_taxonomy_code_id = ?";

  public static final String GET_EQUIVALENT_CODE = "SELECT source_display_code, target_taxonomy_code_id, target_display_code, target_title, target_framework_id from taxonomy_code_mapping where source_taxonomy_code_id = ?";

  public static final String SOURCE_TAXONOMY_CODE_ID = "source_taxonomy_code_id";

  public static final String SOURCE_DISPLAY_CODE = "source_display_code";

  public static final String TARGET_TAXONOMY_CODE_ID = "target_taxonomy_code_id";

  public static final String TARGET_DISPLAY_CODE = "target_display_code";

  public static final String TARGET_FRAMEWORK_ID = "target_framework_id";

  public static final String TARGET_TITLE = "target_title";

}
