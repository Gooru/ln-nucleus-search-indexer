package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.TAXONOMY_CODE_MAPPING)
public class TaxonomyCodeMapping extends Model {
  public final static String INTERNAL_TARGET_CODE_TO_SOURCE_CODE = "target_taxonomy_code_id = ?";

  public final static String INTERNAL_SOURCE_CODE_TO_TARGET_CODE = "source_taxonomy_code_id = ?";

  public final static String INTERNAL_SOURCE_CODES_TO_TARGET_CODES = "source_taxonomy_code_id = ANY (?::varchar[])";
  
  public static final String GET_GDT_CODE = "SELECT source_taxonomy_code_id, target_display_code, target_framework_id from taxonomy_code_mapping where target_taxonomy_code_id = ?";

  public static final String GET_EQUIVALENT_CODE = "SELECT source_display_code, target_taxonomy_code_id, target_display_code, target_title, target_framework_id, target_code_type from taxonomy_code_mapping where source_taxonomy_code_id = ?";

  public static final String SOURCE_TAXONOMY_CODE_ID = "source_taxonomy_code_id";

  public static final String SOURCE_DISPLAY_CODE = "source_display_code";

  public static final String TARGET_TAXONOMY_CODE_ID = "target_taxonomy_code_id";

  public static final String TARGET_DISPLAY_CODE = "target_display_code";

  public static final String TARGET_FRAMEWORK_ID = "target_framework_id";

  public static final String TARGET_TITLE = "target_title";
  
  public static final String TARGET_CODE_TYPE = "target_code_type";

}
