package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@Table("content")
@IdName("id")
public class Content extends Model {
  public static final String CONTENT_FORMAT_RESOURCE = "resource";
  public static final String CONTENT_FORMAT_QUESTION = "question";
  
}
