package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gooru.nucleus.search.indexers.app.repositories.entities.MachineClassifiedContents;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class MachineClassifyContentRepositoryImpl extends BaseIndexRepo implements MachineClassifyContentRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(MachineClassifyContentRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @SuppressWarnings("unchecked")
  @Override
  public void saveMachineClassifiedDomains(String id, Map<String, Object> data) {
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      db.openTransaction();
      db.exec(MachineClassifiedContents.INSERT_QUERY_DOMAINS, 
              UUID.fromString((String) data.get("id")), 
              toPostgresArrayString((Set<String>) data.getOrDefault("machine_classified_domains", new HashSet<>())),
              toPostgresArrayString((Set<String>) data.getOrDefault("machine_classified_domains", new HashSet<>())));
      db.commitTransaction();
      LOGGER.info("Successfully stored machine classified domains for content : {}", id);
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("MCCRI:saveMachineClassifiedTags: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void saveMachineClassifiedTags(String id, Map<String, Object> data) {
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      db.openTransaction();
      db.exec(MachineClassifiedContents.INSERT_QUERY_TAGS, 
              UUID.fromString((String) data.get("id")), 
              toPostgresArrayString((Set<String>) data.getOrDefault("machine_classified_tags", new HashSet<>())),
              toPostgresArrayString((Set<String>) data.getOrDefault("machine_classified_tags", new HashSet<>())));
      db.commitTransaction();
      LOGGER.info("Successfully stored machine classified tags for content : {}", id);
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("MCCRI:saveMachineClassifiedTags: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
  @Override
  public Boolean hasDomainClassification(String resourceId) {
    DB db = getDefaultDataSourceDBConnection();
    Boolean returnValue = false;
    try {
      openDefaultDBConnection(db);
      LazyList<MachineClassifiedContents> result =
              MachineClassifiedContents.where(MachineClassifiedContents.FETCH_DOMAIN_CLASSIFICATION, resourceId);

      if (result != null && result.size() > 0 && (((MachineClassifiedContents) result.get(0)).get("id") != null
              && (((MachineClassifiedContents) result.get(0)).get("id").toString()).equalsIgnoreCase(resourceId))) {
        returnValue = true;
      }
    } catch (Exception ex) {
      LOGGER.error("MCCRI:hasDomainClassification: Failed to fetch domain classification for id : {} : EX : {} ", resourceId,  ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return returnValue;
  }
  
  @Override
  public Boolean hasStandardClassification(String resourceId) {
    DB db = getDefaultDataSourceDBConnection();
    Boolean returnValue = false;
    try {
      openDefaultDBConnection(db);
      LazyList<MachineClassifiedContents> result =
              MachineClassifiedContents.where(MachineClassifiedContents.FETCH_STANDARD_CLASSIFICATION, resourceId);

      if (result != null && result.size() > 0 && (((MachineClassifiedContents) result.get(0)).get("id") != null
              && (((MachineClassifiedContents) result.get(0)).get("id").toString()).equalsIgnoreCase(resourceId))) {
        returnValue = true;
      }
    } catch (Exception ex) {
      LOGGER.error("MCCRI:hasDomainClassification: Failed to fetch domain classification for id : {} : EX : {} ", resourceId,  ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return returnValue;
  }
  
  @Override
  public JsonObject getMachineClassifiedContents(String id) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      db.openTransaction();
      MachineClassifiedContents result = MachineClassifiedContents.findById(getPGObject("id", UUID_TYPE, id));

      if (result != null && ((result.getString("machine_classified_domains") != null && !result.getString("machine_classified_domains").equalsIgnoreCase("{}")) || (result.getString("machine_classified_tags") != null  && !result.getString("machine_classified_tags").equalsIgnoreCase("{}")))) {
       returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
      db.commitTransaction();
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("MCCRI:getMachineClassifiedContents: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
    return returnValue;
  }
  
}
