package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public interface TaxonomyRepository {

  static TaxonomyRepository instance() {
    return new TaxonomyRepositoryImpl();
  }
  List<Map> getTaxonomyData(String codeId, String label);

}
