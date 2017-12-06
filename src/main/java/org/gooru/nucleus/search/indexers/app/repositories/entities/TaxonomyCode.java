package org.gooru.nucleus.search.indexers.app.repositories.entities;

import java.util.Arrays;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.TAXONOMY_CODE)
@IdName(SchemaConstants.ID)
public class TaxonomyCode extends Model {

  public static final String ID = "id";
  public static final String CODE = "code";
  public static final String TITLE = "title";
  public static final String DESCRIPTION = "description";

  public static final String SOURCE_TAXONOMY_CODE_ID = "source_taxonomy_code_id";

  public static final String SOURCE_DISPLAY_CODE = "source_display_code";

  public static final String TARGET_TAXONOMY_CODE_ID = "target_taxonomy_code_id";

  public static final String TARGET_DISPLAY_CODE = "target_display_code";

  public static final String TARGET_FRAMEWORK_ID = "target_framework_id";

  public static final String TARGET_TITLE = "target_title";
  
  public static final String PARENT_TAXONOMY_CODE_ID = "parent_taxonomy_code_id";
  
  public final static String FETCH_TAXONOMY_CODE = "id = ?";
  
  public final static String FETCH_GDT_LTS = "code_type = 'learning_target_level_0' and standard_framework_id = ?";

  public final static String FETCH_GDT_STDS = "code_type IN ('standard_level_1','standard_level_2') and standard_framework_id = ?";

  public final static String FETCH_GDT_LTS_STDS = "code_type IN ('standard_level_1','standard_level_2','learning_target_level_0') and standard_framework_id = ?";

  public final static String FETCH_GDT_LT_CODES = "select id, code_type, parent_taxonomy_code_id from taxonomy_code where code_type = 'learning_target_level_0' and standard_framework_id = ? order by updated_at asc limit ? offset ?";

  public final static String FETCH_GDT_STANDARD_CODES = "select id, code_type from taxonomy_code where code_type IN ('standard_level_1','standard_level_2') and standard_framework_id = ? order by updated_at asc limit ? offset ?";

  public final static String FETCH_LTS = "code_type = 'learning_target_level_0'";

  public final static String FETCH_STDS = "code_type IN ('standard_level_1','standard_level_2')";

  public final static String FETCH_LTS_STDS = "code_type IN ('standard_level_1','standard_level_2','learning_target_level_0')";

  public final static String FETCH_LT_CODES = "select id, code_type, parent_taxonomy_code_id from taxonomy_code where code_type = 'learning_target_level_0' order by updated_at asc limit ? offset ?";

  public final static String FETCH_STANDARD_CODES = "select id, code_type from taxonomy_code where code_type IN ('standard_level_1','standard_level_2') order by updated_at asc limit ? offset ?";

  public final static String FETCH_TAXONOMY_CODES = "id = ANY (?::varchar[])";
  
  public static final List<String> RESPONSE_FIELDS = Arrays.asList("title", "code");

}

