package org.gooru.nucleus.search.indexers.app.repositories.entities;

import java.util.Arrays;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author Renuka
 */
@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.SIGNATURE_ITEMS)
@IdName(SchemaConstants.ID)
public class SignatureItems extends Model {
  
  public static final String FETCH_SIGNATURE_ITEMS = "(competency_gut_code = ? OR micro_competency_gut_code = ?) AND item_format = ?";

  public static final List<String> RESPONSE_FIELDS = Arrays.asList("item_id", "efficacy", "engagement");

  public final static String FETCH_CURATED_SUGGESTION_BY_C_OR_MC = "(competency_gut_code = ? or micro_competency_gut_code = ?) and is_curated = true and item_format = ?";

  public final static String INSERT_QUERY = "insert into signature_items (competency_gut_code, micro_competency_gut_code,"
          + "performance_range, item_id, item_format, primary_language) values (?,?,?,?,?,?)";
  
  public final static String DELETE_RECORDS = "delete from signature_items where is_curated = ? and item_format = ?";
  
  public final static String FETCH_CURATED_SI_BY_ITEM_ID = "item_id = ? and is_curated = true";

}
