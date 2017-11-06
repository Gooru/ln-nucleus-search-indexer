package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.CONCEPT_BASED_RESOURCE_SUGGEST)
@IdName(SchemaConstants.ID)
public class ConceptBasedResourceSuggest extends Model {

  public final static String FETCH_SUGGESTION_BY_C_OR_MC = "competency_internal_code = ? or micro_competency_internal_code = ?";
  
  public final static String INSERT_QUERY = "insert into concept_based_resource_suggest (competency_internal_code,micro_competency_internal_code,"
          + "ctx_type,performance_range,suggest_type,ids_to_suggest) values (?,?,?,?,?,?::text[])";
  
}
