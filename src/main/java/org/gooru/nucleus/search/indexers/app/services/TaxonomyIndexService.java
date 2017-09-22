package org.gooru.nucleus.search.indexers.app.services;

public interface TaxonomyIndexService {

  static TaxonomyIndexService instance(){
    return new TaxonomyIndexServiceImpl();
  }
  
  void deleteIndexedTaxonomy(String key, String type) throws Exception;

}
