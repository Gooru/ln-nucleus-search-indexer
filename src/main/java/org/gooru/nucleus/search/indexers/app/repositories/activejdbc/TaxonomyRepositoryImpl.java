package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Taxonomy;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TaxonomyRepositoryImpl extends BaseIndexRepo implements TaxonomyRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyRepositoryImpl.class);

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getTaxonomyData(String codeId, String level) {
    String query = null;
    switch(level) {
    case IndexerConstants.SUBJECT :
      query = Taxonomy.GET_SUBJECT_QUERY;
      break;
    case IndexerConstants.COURSE :
      query = Taxonomy.GET_COURSE_QUERY;
      break;
    case IndexerConstants.DOMAIN :
      query = Taxonomy.GET_DOMAIN_QUERY;
      break;
    case IndexerConstants.STANDARD :
    case IndexerConstants.LEARNING_TARGET :
      query = Taxonomy.GET_CODE;
      break;
    }
    List<Map> taxMetaList = null;
    if (query != null) {
      DB db = getDefaultDataSourceDBConnection();
      try{
        openConnection(db);
        taxMetaList = db.findAll(query, codeId);
        if (taxMetaList.size() < 1) {
          LOGGER.warn("Taxonomy info for {} level for id : {} not present in DB", level, codeId);
        }
      }
      catch(Exception ex){
        LOGGER.error("Failed to fetch taxonomy details ", ex);
      }
      finally {
        closeDBConn(db);
      }
    }
    return taxMetaList;
  }
  
  @SuppressWarnings("rawtypes")
  public Map getGDTCode(String targetCodeId) {
    Map gdtData = null;
    String query = TaxonomyCodeMapping.GET_GDT_CODE;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      List<Map> gdtDataList = db.findAll(query, targetCodeId);
      if (gdtDataList.size() > 0 && gdtDataList.get(0) != null && gdtDataList.get(0).containsKey(TaxonomyCodeMapping.SOURCE_TAXONOMY_CODE_ID)) {
        gdtData = gdtDataList.get(0);
        return gdtData;
      } else {
        LOGGER.warn("GDT code for {} standard : {} not present in DB", targetCodeId);
      }
    }
    catch(Exception ex){
      LOGGER.error("Failed to fetch taxonomy details ", ex);
    }
    finally {
      closeDBConn(db);
    }
    return gdtData;
  }
  
  @SuppressWarnings("rawtypes")
  public List<Map> getEquivalentCompetencies(String sourceCodeId) {
    List<Map> equivalentCodes = null;
    String query = TaxonomyCodeMapping.GET_EQUIVALENT_CODE;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      equivalentCodes = db.findAll(query, sourceCodeId);
      if (equivalentCodes.size() < 1) {
        LOGGER.warn("Equivalent codes for {} standard : {} not present in DB", sourceCodeId);
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch Equivalent codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return equivalentCodes;
  }

  @Override
  public JsonObject getCrosswalkCodes(String sourceCodeId) {
    LOGGER.debug("TaxonomyRepositoryImpl : getCrosswalkCodes : " + sourceCodeId);
    JsonObject returnObject = new JsonObject();
    JsonArray crosswalkArray = null;
    try {
      LazyList<TaxonomyCodeMapping> crosswalkCodes = TaxonomyCodeMapping.where(TaxonomyCodeMapping.INTERNAL_SOURCE_CODE_TO_TARGET_CODE, sourceCodeId);
      if (crosswalkCodes == null || crosswalkCodes.size() < 1) {
        LOGGER.debug("Crosswalk codes for GUT Code : {} not present in DB", sourceCodeId);
        return null;
      }
      crosswalkArray = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(crosswalkCodes));
      returnObject.put("id", sourceCodeId);
      returnObject.put("crosswalk", crosswalkArray);
    } catch (Exception e) {
      LOGGER.error("Not able to fetch crosswalk codes for GUT : {} error : {}", sourceCodeId, e);
    }
    return returnObject;
  }
  
  @Override
  public JsonObject getGdtMapping(String targetCodeId) {

    LOGGER.debug("TaxonomyRepositoryImpl : getGdtMapping : " + targetCodeId);
    TaxonomyCodeMapping result = null;
    LazyList<TaxonomyCodeMapping> list = TaxonomyCodeMapping.where(TaxonomyCodeMapping.INTERNAL_TARGET_CODE_TO_SOURCE_CODE, targetCodeId);

    if (list != null && list.size() > 0) {
      result = list.get(0);
    }

    JsonObject returnValue = null;
    if (result != null) {
      returnValue = new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
  }
}
