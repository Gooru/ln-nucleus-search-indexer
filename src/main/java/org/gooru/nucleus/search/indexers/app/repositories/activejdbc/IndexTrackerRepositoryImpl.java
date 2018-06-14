package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.UUID;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.CollectionIndexDelete;
import org.gooru.nucleus.search.indexers.app.repositories.entities.IndexerJobStatus;
import org.gooru.nucleus.search.indexers.app.repositories.entities.ResourceIndexDelete;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class IndexTrackerRepositoryImpl extends BaseIndexRepo implements IndexTrackerRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexTrackerRepositoryImpl.class);

  @Override
  public void saveDeletedResource(String id, JsonObject data, List<String> attributes) {
    try {
      ResourceIndexDelete.createIt(EntityAttributeConstants.GOORU_OID, UUID.fromString(data.getString(EntityAttributeConstants.GOORU_OID)), EntityAttributeConstants.INDEX_TYPE, data.getString(EntityAttributeConstants.INDEX_TYPE));
      LOGGER.info("Successfully tracked deleted resource : {}", data.getString(EntityAttributeConstants.GOORU_OID));
    } catch (Exception e) {
      LOGGER.error("Save Failed for ID : {} : Error : {}", data.getString(EntityAttributeConstants.GOORU_OID),  e.getMessage());
    }
  }

  @Override
  public void saveDeletedCollection(String id, JsonObject data, List<String> insertCollectionAllowedFields) {
    try {
      CollectionIndexDelete.createIt(EntityAttributeConstants.GOORU_OID, UUID.fromString(data.getString(EntityAttributeConstants.GOORU_OID)), EntityAttributeConstants.INDEX_TYPE, data.getString(EntityAttributeConstants.INDEX_TYPE));
      LOGGER.info("Successfully tracked deleted collection : {}", data.getString(EntityAttributeConstants.GOORU_OID));
    } catch (Exception e) {
      LOGGER.error("Save Failed for ID : {} : Error : {}", data.getString(EntityAttributeConstants.GOORU_OID),  e.getMessage());
    }
  }
  
  @Override
    public void saveJobStatus(String jobKey, String status) {
        DB db = getTrackerDataSourceDBConnection();
        try {
            openTrackerDBConnection(db);
            int d = db.exec("update indexer_job_status set status = ?, updated_at = now() where key = ?", status, jobKey);
            db.commitTransaction();
            LOGGER.info("Successfully stored status : {} for job :{} updated count : {}", status, jobKey, d);
        } catch (Exception e) {
            LOGGER.error("Save Failed for jobkey : {} : Error : {}", jobKey, e.getMessage());
        }
        closeTrackerDBConn(db);
    }
  
    @Override
    public LazyList<IndexerJobStatus> getJobStatus(String jobKey) {
        LazyList<IndexerJobStatus> jobDetails = null;
        DB db = getTrackerDataSourceDBConnection();
        try {
            openTrackerDBConnection(db);
            jobDetails = IndexerJobStatus.where(IndexerJobStatus.FETCH_JOB_DETAILS, jobKey);
            if (jobDetails.size() < 1) {
                LOGGER.warn("Job details for job: {} not present in DB", jobKey);
            }
        } catch (Exception e) {
            LOGGER.error("Not able to fetch job details for job key : {} error : {}", jobKey, e);
        }
        closeTrackerDBConn(db);
        return jobDetails;
    }
}
