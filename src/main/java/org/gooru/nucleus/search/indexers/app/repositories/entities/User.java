package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName(IndexerConstants.DEFAULT_DATABASE_NAME)
@Table("user_demographic")
@IdName("id")
public class User extends Model {
  
  public static final String GET_USER = "SELECT u.id as userId, * from user_demographic u left join user_identity i on u.id = i.user_id where u.id = ?::uuid";

}
