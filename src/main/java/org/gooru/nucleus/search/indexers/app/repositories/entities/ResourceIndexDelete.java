package org.gooru.nucleus.search.indexers.app.repositories.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName("search")
@Table("resource_index_delete_tracker")
@IdName("gooru_oid")
public class ResourceIndexDelete extends Model {

  public static final List<String> INSERT_RESOURCE_ALLOWED_FIELDS = Arrays.asList("gooru_oid","index_type");

}
