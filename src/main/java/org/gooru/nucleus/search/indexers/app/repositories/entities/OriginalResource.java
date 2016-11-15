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
@Table(SchemaConstants.ORIGINAL_RESOURCE)
@IdName(SchemaConstants.ID)
public class OriginalResource extends Model {
  
  public static final String CONTENT_FORMAT_RESOURCE = "resource";
  public static final String FETCH_DELETED_QUERY = "id = ?::uuid and is_deleted = ?";
  public static final String IS_DELETED = "is_deleted";
  public static final String FETCH_USER_ORIGINAL_RESOURCES = "creator_id = ?::uuid and is_deleted = ?";
  public static final String METADATA = "metadata";
  
}
