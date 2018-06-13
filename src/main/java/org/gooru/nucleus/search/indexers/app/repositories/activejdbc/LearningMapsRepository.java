package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.Map;

public interface LearningMapsRepository {


  static LearningMapsRepository instance() {
    return new LearningMapsRepositoryImpl();
  }
    
  void updateRQCACULLearningMaps(Map<String, Object> data);

}
