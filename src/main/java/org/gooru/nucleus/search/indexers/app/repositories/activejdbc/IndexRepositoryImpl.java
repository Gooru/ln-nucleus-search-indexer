package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.gooru.nucleus.search.indexers.app.repositories.entities.SignatureItems;
import org.gooru.nucleus.search.indexers.app.repositories.entities.SignatureResources;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class IndexRepositoryImpl extends BaseIndexRepo implements IndexRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexRepositoryImpl.class);

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getMetadata(String referenceIds) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);
    List<Map> metadataReference = db.findAll(Content.FETCH_METADATA, referenceIds);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", referenceIds);
    }
    closeDBConn(db);
    return metadataReference;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getLicenseMetadata(int metadataId) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);

    List<Map> metadataReference = db.findAll(Content.FETCH_LICENSE_METADATA, metadataId);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", metadataId);
    }
    closeDBConn(db);
    return metadataReference;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getTwentyOneCenturySkill(String referenceIds) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);
    List<Map> metadataReference = db.findAll(Content.FETCH_TWENTY_ONE_CENTURY_SKILL, referenceIds);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", referenceIds);
    }
    closeDBConn(db);
    return metadataReference;
  }
  
  @Override
  public JsonObject getSignatureResourcesByContentId(String contentId, String contentType) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);

    JsonObject returnValue = null;
    LazyList<SignatureResources> contents = SignatureResources.findBySQL(SignatureResources.FETCH_SIGNATURE_RESOURCES, contentId, contentType);
    if (contents.size() < 1) {
      LOGGER.warn("Content id: {} not present in DB", contentId);
    }
    if (contents.size() > 0) {
      SignatureResources content = contents.get(0);
      if (content != null) {
        returnValue = new JsonObject(content.toJson(false));
      }
    }
    closeDBConn(db);
    return returnValue;
  }
  
  @Override
  public JsonArray getSignatureResourcesByCodeId(String codeId) {
    JsonArray responses = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);

      LazyList<SignatureResources> contents = SignatureResources.where(SignatureResources.FETCH_SIGNATURE_RESOURCES_BY_CODE, codeId, codeId);
      if (contents.size() < 1) {
        LOGGER.warn("Code id: {} not present in signature_resources DB", codeId);
      } else {
        responses = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(contents));
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch signature_resources : ", ex);
    } finally {
      closeDBConn(db);
    }
    return responses;
  }
  
  @Override
  public JsonArray getSignatureItems(String codeId, String contentType) {
    JsonArray responses = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);

      LazyList<SignatureItems> contents = SignatureItems.where(SignatureItems.FETCH_SIGNATURE_ITEMS, codeId, codeId, contentType);
      if (contents.size() < 1) {
        LOGGER.warn("Code id: {} not present in signature_items DB", codeId);
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
