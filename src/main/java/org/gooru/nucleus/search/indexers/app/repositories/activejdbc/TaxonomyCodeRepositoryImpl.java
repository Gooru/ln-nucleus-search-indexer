package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class TaxonomyCodeRepositoryImpl extends BaseIndexRepo implements TaxonomyCodeRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyCodeRepositoryImpl.class);

  @Override
  public JsonObject getTaxonomyCode(String codeId) {
    TaxonomyCode result = TaxonomyCode.findById(codeId);

    JsonObject returnValue = null;
    if (result != null) {
      returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
  }
  
  @Override
  public JsonObject getCode(String codeId) {
    DB db = getDefaultDataSourceDBConnection();
    JsonObject returnValue = null;
    try {
      openConnection(db);
      TaxonomyCode result = TaxonomyCode.findById(codeId);

      if (result != null) {
        returnValue = new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch taxonomy code ", ex);
    } finally {
      closeDBConn(db);
    }
    return returnValue;
  }

}
