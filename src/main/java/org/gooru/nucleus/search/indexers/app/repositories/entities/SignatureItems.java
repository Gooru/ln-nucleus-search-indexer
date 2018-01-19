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
  
  public static final String FETCH_SIGNATURE_ITEMS = "(competency_internal_code = ? OR micro_competency_internal_code = ?) AND item_format = ?";

  public static final List<String> RESPONSE_FIELDS = Arrays.asList("item_id", "efficacy", "engagement");

}
