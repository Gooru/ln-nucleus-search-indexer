package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

public interface LearningMapsRepository {


  static LearningMapsRepository instance() {
    return new LearningMapsRepositoryImpl();
  }
  
  void updateRQCALearningMaps(List<Map<String, Object>> array);
  
  void updateCULForLearningMaps(Map<String, Object> data);

  void updateRQCACULLearningMaps(Map<String, Object> data);

}
