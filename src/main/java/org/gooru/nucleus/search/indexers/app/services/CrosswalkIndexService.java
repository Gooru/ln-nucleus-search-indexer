package org.gooru.nucleus.search.indexers.app.services;

public interface CrosswalkIndexService {

  static CrosswalkIndexService instance(){
    return new CrosswalkIndexServiceImpl();
  }
  
  void deleteIndexedCrosswalk(String key, String type) throws Exception;

}
