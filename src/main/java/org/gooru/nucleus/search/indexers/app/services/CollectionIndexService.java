package org.gooru.nucleus.search.indexers.app.services;

public interface CollectionIndexService {

  static CollectionIndexService instance(){
    return new CollectionIndexServiceImpl();
  }
  void deleteIndexedCollection(String key, String type) throws Exception;

}
