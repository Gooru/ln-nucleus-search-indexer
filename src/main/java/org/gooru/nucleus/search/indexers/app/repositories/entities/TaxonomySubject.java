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
@Table(SchemaConstants.TAXONOMY_SUBJECT)
@IdName(SchemaConstants.ID)
public class TaxonomySubject extends Model {

  public static final String FETCH_GUT_SUBJECT_BY_TITLE = "title = ? and standard_framework_id is null";

}
