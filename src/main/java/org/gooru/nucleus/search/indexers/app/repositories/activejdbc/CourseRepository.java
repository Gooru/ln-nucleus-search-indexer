package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

public interface CourseRepository {

  static CourseRepository instance() {
    return new CourseRepositoryImpl();
  }
  @SuppressWarnings("rawtypes")
  List<Map> getCourse(String courseId);
}
