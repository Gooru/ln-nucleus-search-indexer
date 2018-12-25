package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.SignatureResources;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SignatureResourcesRepositoryImpl extends BaseIndexRepo implements SignatureResourcesRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignatureResourcesRepositoryImpl.class);

  @Override
  public Boolean hasCuratedSuggestion(String codeId) {
    DB db = getDefaultDataSourceDBConnection();
    Boolean returnValue = false;
    try {
      openDefaultDBConnection(db);
      LazyList<SignatureResources> result =
              SignatureResources.where(SignatureResources.FETCH_CURATED_SUGGESTION_BY_C_OR_MC, codeId, codeId);

      if (result != null && result.size() > 0) {
        returnValue = true;
      }
    } catch (Exception ex) {
      LOGGER.error("SRRI:hasCuratedSuggestion: Failed to fetch curated suggestions ", ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return returnValue;
  }

  @Override
  public void saveSuggestions(String id, JsonObject data) {
   DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      db.openTransaction();
      db.exec(SignatureResources.INSERT_QUERY, data.getString(EntityAttributeConstants.COMPETENCY_GUT_CODE), data.getString(EntityAttributeConstants.MICRO_COMPETENCY_GUT_CODE),
                  data.getString(EntityAttributeConstants.PERFORMANCE_RANGE), data.getString(EntityAttributeConstants.ITEM_ID), data.getString(EntityAttributeConstants.ITEM_FORMAT));      
      db.commitTransaction();
      LOGGER.info("Successfully populated signature resources for code : {}", id);
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("SRRI:saveSuggestions: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
  @Override
  public void deleteSuggestions() {
   DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      db.openTransaction();
      db.exec(SignatureResources.DELETE_RECORDS, false);
      db.commitTransaction();
      LOGGER.info("Successfully deleted {} signature resources");
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("SRRI:deleteSuggestions: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
  @Override
  public JsonArray getSignatureResourcesByGutCode(String gutCode) {
    JsonArray responses = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);

      LazyList<SignatureResources> contents = SignatureResources.where(SignatureResources.FETCH_SIGNATURE_RESOURCE_BY_C_OR_MC, gutCode, gutCode);
      if (contents.size() < 1) {
        LOGGER.warn("Code id: {} not present in signature_resources DB for contentType : {}", gutCode);
      } else {
        responses = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(contents));
      }
    } catch (Exception ex) {
      LOGGER.error("SRRI::Failed to fetch signature_resources : ", ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return responses;
  }
  
  @Override
  public Boolean isCuratedSignatureResourceByItemId(String itemId) {
    DB db = getDefaultDataSourceDBConnection();
    Boolean returnValue = false;
    try {
      openDefaultDBConnection(db);
      LazyList<SignatureResources> result =
              SignatureResources.where(SignatureResources.FETCH_CURATED_SR_BY_ITEM_ID, itemId);

      if (result != null && result.size() > 0) {
        returnValue = true;
      }
    } catch (Exception ex) {
      LOGGER.error("SRRI:hasCuratedSR: Failed to fetch curated SR ", ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return returnValue;
  }

}
