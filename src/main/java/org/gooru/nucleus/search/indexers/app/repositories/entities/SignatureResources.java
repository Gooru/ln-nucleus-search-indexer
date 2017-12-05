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
@Table(SchemaConstants.SIGNATURE_RESOURCES)
@IdName(SchemaConstants.ID)
public class SignatureResources extends Model {
  
  public static final String FETCH_SIGNATURE_RESOURCES =
          "SELECT AVG(efficacy) AS efficacy, AVG(engagement) AS engagement from signature_resources WHERE resource_id = ? AND resource_type = ?";

}
