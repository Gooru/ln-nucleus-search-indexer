package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Language;
import org.gooru.nucleus.search.indexers.app.repositories.entities.SignatureResources;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCourseOld;
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
    openDefaultDBConnection(db);
    List<Map> metadataReference = db.findAll(Content.FETCH_METADATA, referenceIds);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", referenceIds);
    }
    closeDefaultDBConn(db);
    return metadataReference;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getLicenseMetadata(int metadataId) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);

    List<Map> metadataReference = db.findAll(Content.FETCH_LICENSE_METADATA, metadataId);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", metadataId);
    }
    closeDefaultDBConn(db);
    return metadataReference;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getTwentyOneCenturySkill(String referenceIds) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);
    List<Map> metadataReference = db.findAll(Content.FETCH_TWENTY_ONE_CENTURY_SKILL, referenceIds);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", referenceIds);
    }
    closeDefaultDBConn(db);
    return metadataReference;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getLanguages(Integer languageId) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);
    LOGGER.debug("IndexRepositoryImpl : getLanguages : " + languageId);
    List<Map> languageList = db.findAll(Language.FETCH_LANGUAGE_CODE, languageId);    
    if (languageList.size() < 1) {
      LOGGER.warn("Language id: {} not present in DB", languageList);
    }
    closeDefaultDBConn(db);
    return languageList;
  }
  
  @Override
  public JsonObject getSignatureResourcesByContentId(String contentId, String contentType) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);

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
    closeDefaultDBConn(db);
    return returnValue;
  }
  
  @Override
  public JsonArray getSignatureResourcesByCodeId(String codeId) {
    JsonArray responses = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);

      LazyList<SignatureResources> contents = SignatureResources.where(SignatureResources.FETCH_SIGNATURE_RESOURCES_BY_CODE, codeId, codeId);
      if (contents.size() < 1) {
        LOGGER.warn("Code id: {} not present in signature_resources DB", codeId);
      } else {
        responses = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(contents));
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch signature_resources : ", ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return responses;
  }
 
  @Override
  public JsonArray getSignatureResourcesByGutCode(String gutCodeId) {
    JsonArray responses = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);

      LazyList<SignatureResources> contents = SignatureResources.where(SignatureResources.FETCH_SIGNATURE_RESOURCES_BY_GUT_CODE, gutCodeId, gutCodeId);
      if (contents.size() < 1) {
        LOGGER.warn("Code id: {} not present in signature_resources DB", gutCodeId);
      } else {
        responses = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(contents));
      }
    } catch (Exception ex) {
      LOGGER.error("getSignatureResourcesByGutCode::Failed to fetch signature_resources : ", ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return responses;
  }
  
  @Override
  public String getCurrentCourseCodeByOldTitle(String courseTitle) {
    String response = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);

      LazyList<TaxonomyCourseOld> contents = TaxonomyCourseOld.where(TaxonomyCourseOld.FETCH_COURSE_BY_OLD_TITLE, courseTitle);
      if (contents.size() < 1) {
        LOGGER.warn("Course Title: {} not present in taxonomy_course_old table", courseTitle);
      } else {
        response = contents.get(0).getString(EntityAttributeConstants.ID);
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch taxonomy course : ", ex);
    } finally {
      closeDefaultDBConn(db);
    }
    return response;
  }
 
 
}
