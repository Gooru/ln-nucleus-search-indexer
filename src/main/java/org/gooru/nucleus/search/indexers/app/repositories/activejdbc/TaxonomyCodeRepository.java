package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface TaxonomyCodeRepository {

  static TaxonomyCodeRepository instance() {
    return new TaxonomyCodeRepositoryImpl();
  }
  
  JsonObject getTaxonomyCode(String codeId);

  JsonObject getCode(String codeId);

  Long getStandardLtsCountByFramework(String frameworkCode);

  JsonArray getLTCodeByFrameworkAndOffset(String frameworkCode, Integer limit, Long offset);

  JsonArray getStandardCodeByFrameworkAndOffset(String frameworkCode, Integer limit, Long offset);

  Long getStandardCountByFramework(String frameworkCode);

  Long getLTCountByFramework(String frameworkCode);
  
  Long getStandardLtsCount();

  JsonArray getLTCodeByOffset(Integer limit, Long offset);

  JsonArray getStandardCodeByOffset(Integer limit, Long offset);

  Long getStandardCount();

  Long getLTCount();

  JsonArray getStdLTCodeByFrameworkAndOffset(String frameworkCode, Integer limit, Long offset);

  JsonObject getGutCode(String codeId);

}
