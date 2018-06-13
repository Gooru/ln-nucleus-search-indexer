package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface MachineClassifyContentRepository {

  static MachineClassifyContentRepository instance() {
    return new MachineClassifyContentRepositoryImpl();
  }
  
  void saveMachineClassifiedTags(String id, Map<String, Object> data);

  Boolean hasDomainClassification(String resourceId);

  JsonObject getMachineClassifiedContents(String id);

  Boolean hasStandardClassification(String resourceId);

  void saveMachineClassifiedDomains(String id, Map<String, Object> data);

}
