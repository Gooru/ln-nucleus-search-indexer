package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@Table("user_demographic")
@IdName("id")
public class User extends Model {
  
  public static final String GET_USER = "SELECT * from user_demographic u inner join user_identity i on u.id = i.user_id where u.id = ?::uuid";

}
