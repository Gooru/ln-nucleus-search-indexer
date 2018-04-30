package org.gooru.nucleus.search.indexers.app.services;

public interface GutIndexService {

  static GutIndexService instance(){
    return new GutIndexServiceImpl();
  }
  
  void deleteIndexedGut(String key, String type) throws Exception;

}
