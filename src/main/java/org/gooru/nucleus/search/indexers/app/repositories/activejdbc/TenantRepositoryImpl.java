package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;

import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Tenant;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class TenantRepositoryImpl extends BaseIndexRepo implements TenantRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(TenantRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getTenant(String tenantId) {
    LOGGER.debug("TenantRepositoryImpl:getTenant: " + tenantId);

    Tenant result = Tenant.findById(getPGObject("id", UUID_TYPE, tenantId));

    JsonObject returnValue = null;
    if (result != null && result.getString(Tenant.STATUS).equalsIgnoreCase(IndexerConstants.ACTIVE)) {
     returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
  }
  
  @Override
  public JsonObject findByTenantId(String tenantId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      Tenant result = Tenant.findById(getPGObject("id", UUID_TYPE, tenantId));
      if (result != null && result.getString(Tenant.STATUS).equalsIgnoreCase(IndexerConstants.ACTIVE)) {
        returnValue = new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
      return returnValue;
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch tenant details ", ex);
    } finally {
      closeDBConn(db);
    }
    return returnValue;
  }

  
  private PGobject getPGObject(String field, String type, String value) {
    PGobject pgObject = new PGobject();
    pgObject.setType(type);
    try {
      pgObject.setValue(value);
      return pgObject;
    } catch (SQLException e) {
      LOGGER.error("Not able to set value for field: {}, type: {}, value: {}", field, type, value);
      return null;
    }
  }
}
