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

  public final static String INTERNAL_SOURCE_CODES_TO_TARGET_CODES = "source_taxonomy_code_id = ANY (?::varchar[])";
}
