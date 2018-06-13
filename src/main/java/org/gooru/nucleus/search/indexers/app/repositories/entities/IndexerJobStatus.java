package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;
/**
 * @author Renuka
 */
@DbName(SchemaConstants.TRACKER_DATABASE_NAME)
@Table(SchemaConstants.INDEXER_JOB_STATUS)
@IdName(SchemaConstants.KEY)
public class IndexerJobStatus extends Model {
  
  public static final String FETCH_JOB_DETAILS = "key = ?";
  
  public static final String INSERT_JOB_DETAILS = "insert into indexer_job_status(key, status, updated_at) values (?, ?, ?)";

}
