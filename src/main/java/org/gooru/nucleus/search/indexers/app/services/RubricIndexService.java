package org.gooru.nucleus.search.indexers.app.services;

public interface RubricIndexService {

  static RubricIndexService instance(){
    return new RubricIndexServiceImpl();
  }
  
  void deleteIndexedRubric(String key, String type) throws Exception;

}
