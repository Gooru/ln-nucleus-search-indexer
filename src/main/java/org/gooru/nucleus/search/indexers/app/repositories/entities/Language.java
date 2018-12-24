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
@Table(SchemaConstants.GOORU_LANGUAGE)
@IdName(SchemaConstants.ID)
public class Language extends Model {
  
  public static final List<String> RESPONSE_FIELDS = Arrays.asList("name", "id");
  
}
