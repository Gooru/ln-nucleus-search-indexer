package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface CourseRepository {

  static CourseRepository instance() {
    return new CourseRepositoryImpl();
  }
  JsonObject getCourse(String courseId);
}
