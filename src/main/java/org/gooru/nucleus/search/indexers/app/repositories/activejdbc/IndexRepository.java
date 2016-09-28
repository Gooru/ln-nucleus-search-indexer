package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public interface IndexRepository {

  static IndexRepository instance() {
    return new IndexRepositoryImpl();
  }

  List<Map> getMetadata(String referenceIds);

  List<Map> getLicenseMetadata(int metadataId);

  List<Map> getTwentyOneCenturySkill(String referenceIds);
}
