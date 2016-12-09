package org.gooru.nucleus.search.indexers.app.services;

public interface ResourceIndexService {

  static ResourceIndexService instance(){
    return new ResourceIndexServiceImpl();
  }
  void deleteIndexedResource(String key, String type) throws Exception;
  void deleteIndexedQuestion(String key, String type) throws Exception;

}
