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
@Table(SchemaConstants.RUBRIC)
@IdName(SchemaConstants.ID)
public class Rubric extends Model {
  public static final String FETCH_DELETED_QUERY = "id = ?::uuid and is_deleted = ?";
  public static final String IS_DELETED = "is_deleted";
  public static final String FETCH_MAPPED_QUESTIONS =
          "select count(distinct content_id) from rubric r inner join content c on r.content_id = c.id where content_id is not null and r.is_deleted = false and c.is_deleted = false and parent_rubric_id =?::uuid";
}
