package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;
/**
 * @author Renuka
 */
@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.TAXONOMY_DOMAIN)
@IdName(SchemaConstants.ID)
public class TaxonomyDomain extends Model {

  public static final String FETCH_DOMAIN_BY_COURSE_AND_FW = "taxonomy_course_id = ? and standard_framework_id = ?";

  public static final String ID = "id";
  
}
