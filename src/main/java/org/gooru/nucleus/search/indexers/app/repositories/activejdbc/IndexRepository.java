package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonObject;

@SuppressWarnings("rawtypes")
public interface IndexRepository {

  static IndexRepository instance() {
    return new IndexRepositoryImpl();
  }

  List<Map> getMetadata(String referenceIds);

  List<Map> getLicenseMetadata(int metadataId);

  List<Map> getTwentyOneCenturySkill(String referenceIds);

  JsonObject getSignatureResources(String contentId, String contentType);
}
