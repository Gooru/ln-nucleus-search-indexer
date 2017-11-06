package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface TenantRepository {

  static TenantRepository instance() {
    return new TenantRepositoryImpl();
  }
  
  JsonObject getTenant(String tenantId);
  
  JsonObject findByTenantId(String tenantId);

}
