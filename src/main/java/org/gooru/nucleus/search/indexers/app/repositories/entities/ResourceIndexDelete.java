package org.gooru.nucleus.search.indexers.app.repositories.entities;

import java.util.Arrays;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author GooruSearchTeam
 */
@DbName(SchemaConstants.TRACKER_DATABASE_NAME)
@Table(SchemaConstants.RESOURCE_INDEX_DELETE_TRACKER)
@IdName(SchemaConstants.ID)
public class ResourceIndexDelete extends Model {

  public static final List<String> INSERT_RESOURCE_ALLOWED_FIELDS = Arrays.asList("gooru_oid","index_type");

}
