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
@Table(SchemaConstants.SIGNATURE_RESOURCES)
@IdName(SchemaConstants.ID)
public class SignatureResources extends Model {
  
  public static final String FETCH_SIGNATURE_RESOURCES =
          "SELECT MAX(efficacy) AS efficacy, MAX(engagement) AS engagement from signature_resources WHERE resource_id = ? AND resource_type = ?";

  public static final String FETCH_SIGNATURE_RESOURCES_BY_CODE =
          "competency_internal_code = ? OR micro_competency_internal_code = ?";

  public static final String FETCH_SIGNATURE_RESOURCES_BY_GUT_CODE =
          "competency_gut_code = ? OR micro_competency_gut_code = ?";

  public static final List<String> RESPONSE_FIELDS = Arrays.asList("resource_id", "efficacy", "engagement");

}
