package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@Table("user_identity")
@IdName("id")
public class UserIdentity extends Model {

  public static final String USERNAME = "username";
  
  public static final String SELECT_USERNAME = "SELECT username FROM user_identity WHERE user_id = ?::uuid";
}
