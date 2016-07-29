package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName("nucleus")
@Table("collection")
@IdName("id")
public class Collection extends Model {
  public static final String COLLECTION = "collection";

  public static final String ASSESSEMENT = "assessment";
  
  public static final String GET_COLLECTION = "format != ?::content_container_type and id = ?::uuid and is_deleted = ?";

  public static final String COLLECTION_QUERY = "format = ?::content_container_type and id = ?::uuid and is_deleted = ?";

  public static final String FETCH_RESOURCE_META = "select * from content where collection_id = ?::uuid and is_deleted = ?";

  public static final String FETCH_DELETED_QUERY = "id = ?::uuid and is_deleted = ?";
  
  public static final String FETCH_USER_COLLECTIONS = "creator_id = ?::uuid or original_creator_id = ?::uuid or owner_id = ?::uuid and is_deleted = ?";
  
  public static final String IS_DELETED = "is_deleted";

}
