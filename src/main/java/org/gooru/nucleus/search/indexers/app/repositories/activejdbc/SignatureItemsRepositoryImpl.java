package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.SignatureItems;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SignatureItemsRepositoryImpl extends BaseIndexRepo implements SignatureItemsRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignatureItemsRepositoryImpl.class);

  @Override
  public Boolean hasCuratedSuggestion(String codeId) {
    DB db = getDefaultDataSourceDBConnection();
    Boolean returnValue = false;
    try {
      openConnection(db);
      LazyList<SignatureItems> result =
              SignatureItems.where(SignatureItems.FETCH_CURATED_SUGGESTION_BY_C_OR_MC, codeId, codeId);

      if (result != null && result.size() > 0) {
        returnValue = true;
      }
    } catch (Exception ex) {
      LOGGER.error("SIRI:hasCuratedSuggestion: Failed to fetch curated suggestions ", ex);
    } finally {
      closeDBConn(db);
    }
    return returnValue;
  }

  @Override
  public void saveSuggestions(String id, JsonObject data) {
   DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      db.openTransaction();
      db.exec(SignatureItems.INSERT_QUERY, data.getString(EntityAttributeConstants.COMPETENCY_GUT_CODE), data.getString(EntityAttributeConstants.MICRO_COMPETENCY_GUT_CODE),
                  data.getString(EntityAttributeConstants.PERFORMANCE_RANGE), data.getString(EntityAttributeConstants.ITEM_ID), data.getString(EntityAttributeConstants.ITEM_FORMAT));      
      db.commitTransaction();
      LOGGER.info("Successfully populated signature items for code : {}", id);
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("SIRI:saveSuggestions: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
  @Override
  public void deleteSuggestions(String itemFormat) {
   DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      db.openTransaction();
      db.exec(SignatureItems.DELETE_RECORDS, false, itemFormat);
      db.commitTransaction();
      LOGGER.info("Successfully deleted {} signature items", itemFormat);
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("SIRI:deleteSuggestions: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
  @Override
  public JsonArray getSignatureItemsByGutCode(String gutCode, String contentType) {
    JsonArray responses = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);

      LazyList<SignatureItems> contents = SignatureItems.where(SignatureItems.FETCH_SIGNATURE_ITEMS, gutCode, gutCode, contentType);
      if (contents.size() < 1) {
        LOGGER.warn("Code id: {} not present in signature_items DB for contentType : {}", gutCode, contentType);
      } else {
        responses = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(contents));
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch signature_items : ", ex);
    } finally {
      closeDBConn(db);
    }
    return responses;
  }

}
