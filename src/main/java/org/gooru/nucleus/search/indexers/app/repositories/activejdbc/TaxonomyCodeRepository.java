package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface TaxonomyCodeRepository {

  static TaxonomyCodeRepository instance() {
    return new TaxonomyCodeRepositoryImpl();
  }
  
  JsonObject getTaxonomyCode(String codeId);

  JsonObject getCode(String codeId);

}
