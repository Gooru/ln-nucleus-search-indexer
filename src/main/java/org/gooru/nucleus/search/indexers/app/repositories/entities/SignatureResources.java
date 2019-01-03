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

  public static final String FETCH_SIGNATURE_RESOURCE_BY_C_OR_MC = "(competency_gut_code = ? OR micro_competency_gut_code = ?)";

  public static final List<String> RESPONSE_FIELDS = Arrays.asList("item_id", "efficacy", "engagement");

  public final static String FETCH_CURATED_SUGGESTION_BY_C_OR_MC = "(competency_gut_code = ? or micro_competency_gut_code = ?) and is_curated = true";

  public final static String INSERT_QUERY = "insert into signature_resources (competency_gut_code, micro_competency_gut_code,"
          + "performance_range, item_id, item_format) values (?,?,?,?,?)";
  
  public final static String DELETE_RECORDS = "delete from signature_items where is_curated = ?";
  
  public final static String FETCH_CURATED_SR_BY_ITEM_ID = "item_id = ? and is_curated = true";

}
