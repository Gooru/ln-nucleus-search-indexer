package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
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
  
  @Override
  public Long getLTCountByFramework(String frameworkCode) {
    LOGGER.debug("TaxonomyRepositoryImpl : getTotalCount() ");
    DB db = getDefaultDataSourceDBConnection();
    Long count = 0L;
    try {
      openConnection(db);
      count = TaxonomyCode.count(TaxonomyCode.FETCH_GDT_LTS, frameworkCode);
    } catch (Exception ex) {
      LOGGER.error("TCRI:getOrderedCode: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return count;
  }
  
  @Override
  public Long getStandardCountByFramework(String frameworkCode) {
    LOGGER.debug("TaxonomyRepositoryImpl : getTotalCount() ");
    DB db = getDefaultDataSourceDBConnection();
    Long count = 0L;
    try {
      openConnection(db);
      count = TaxonomyCode.count(TaxonomyCode.FETCH_GDT_STDS, frameworkCode);
    } catch (Exception ex) {
      LOGGER.error("TCRI:getOrderedCode: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return count;
  }
  
  @Override
  public Long getStandardLtsCountByFramework(String frameworkCode) {
    LOGGER.debug("TaxonomyRepositoryImpl : getTotalCount() ");
    DB db = getDefaultDataSourceDBConnection();
    Long count = 0L;
    try {
      openConnection(db);
      count = TaxonomyCode.count(TaxonomyCode.FETCH_GDT_LTS_STDS, frameworkCode);
    } catch (Exception ex) {
      LOGGER.error("TCRI:getOrderedCode: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return count;
  }
  
  @Override
  public JsonArray getLTCodeByFrameworkAndOffset(String frameworkCode, Integer limit, Long offset) {
    DB db = getDefaultDataSourceDBConnection();
    JsonArray result = null;
    try {
      openConnection(db);
      LazyList<TaxonomyCode> codes =
              TaxonomyCode.findBySQL(TaxonomyCode.FETCH_GDT_LT_CODES, frameworkCode, limit != null ? limit : 10, offset != null ? offset : 0);
      if (codes != null) {
        result = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(codes));
      }
    } catch (Exception ex) {
      LOGGER.error("TCRI:getLTCodeByOffset: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return result;
  }
  
  @Override
  public JsonArray getStandardCodeByFrameworkAndOffset(String frameworkCode, Integer limit, Long offset) {
    DB db = getDefaultDataSourceDBConnection();
    JsonArray result = null;
    try {
      openConnection(db);
      LazyList<TaxonomyCode> codes =
              TaxonomyCode.findBySQL(TaxonomyCode.FETCH_GDT_STANDARD_CODES, frameworkCode, limit != null ? limit : 10, offset != null ? offset : 0);
      if (codes != null) {
        result = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(codes));
      }
    } catch (Exception ex) {
      LOGGER.error("TCRI:getStandardCodeByOffset: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return result;
  }
  
  @Override
  public Long getLTCount() {
    LOGGER.debug("TaxonomyRepositoryImpl : getTotalCount() ");
    DB db = getDefaultDataSourceDBConnection();
    Long count = 0L;
    try {
      openConnection(db);
      count = TaxonomyCode.count(TaxonomyCode.FETCH_GDT_LTS);
    } catch (Exception ex) {
      LOGGER.error("TCRI:getOrderedCode: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return count;
  }
  
  @Override
  public Long getStandardCount() {
    LOGGER.debug("TaxonomyRepositoryImpl : getTotalCount() ");
    DB db = getDefaultDataSourceDBConnection();
    Long count = 0L;
    try {
      openConnection(db);
      count = TaxonomyCode.count(TaxonomyCode.FETCH_STDS);
    } catch (Exception ex) {
      LOGGER.error("TCRI:getOrderedCode: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return count;
  }
  
  @Override
  public Long getStandardLtsCount() {
    LOGGER.debug("TaxonomyRepositoryImpl : getTotalCount() ");
    DB db = getDefaultDataSourceDBConnection();
    Long count = 0L;
    try {
      openConnection(db);
      count = TaxonomyCode.count(TaxonomyCode.FETCH_LTS_STDS);
    } catch (Exception ex) {
      LOGGER.error("TCRI:getOrderedCode: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return count;
  }
  
  @Override
  public JsonArray getLTCodeByOffset(Integer limit, Long offset) {
    DB db = getDefaultDataSourceDBConnection();
    JsonArray result = null;
    try {
      openConnection(db);
      LazyList<TaxonomyCode> codes =
              TaxonomyCode.findBySQL(TaxonomyCode.FETCH_LT_CODES, limit != null ? limit : 10, offset != null ? offset : 0);
      if (codes != null) {
        result = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(codes));
      }
    } catch (Exception ex) {
      LOGGER.error("TCRI:getLTCodeByOffset: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return result;
  }
  
  @Override
  public JsonArray getStandardCodeByOffset(Integer limit, Long offset) {
    DB db = getDefaultDataSourceDBConnection();
    JsonArray result = null;
    try {
      openConnection(db);
      LazyList<TaxonomyCode> codes =
              TaxonomyCode.findBySQL(TaxonomyCode.FETCH_STANDARD_CODES, limit != null ? limit : 10, offset != null ? offset : 0);
      if (codes != null) {
        result = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(codes));
      }
    } catch (Exception ex) {
      LOGGER.error("TCRI:getStandardCodeByOffset: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return result;
  }
  
  @Override
  public JsonArray getStdLTCodeByFrameworkAndOffset(String frameworkCode, Integer limit, Long offset) {
    DB db = getDefaultDataSourceDBConnection();
    JsonArray result = null;
    try {
      openConnection(db);
      LazyList<TaxonomyCode> codes =
              TaxonomyCode.findBySQL(TaxonomyCode.FETCH_GDT_STD_LT_CODES, frameworkCode, limit != null ? limit : 10, offset != null ? offset : 0);
      if (codes != null) {
        result = new JsonArray(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(codes));
      }
    } catch (Exception ex) {
      LOGGER.error("TCRI:getStdLTCodeByFrameworkAndOffset: Failed to fetch taxonomy codes ", ex);
    } finally {
      closeDBConn(db);
    }
    return result;
  }

}
