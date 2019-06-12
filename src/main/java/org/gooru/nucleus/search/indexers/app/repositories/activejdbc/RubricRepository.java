package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface RubricRepository {

  static RubricRepository instance() {
    return new RubricRepositoryImpl();
  }

  JsonObject getRubric(String rubricId);

  JsonObject getDeletedRubric(String rubricId);

  Integer getQuestionCountByRubricId(String rubricId);

  JsonObject getDeletedRubricsOfCourse(String courseId);

  JsonObject getDeletedRubricsOfUnit(String unitId);

  JsonObject getDeletedRubricsOfLesson(String lessonId);

  JsonObject getDeletedRubricsOfItem(String collectionId);

  JsonObject getRubricsOfCourse(String courseId);

  JsonObject getRubricsOfUnit(String unitId);

  JsonObject getRubricsOfLesson(String lessonId);

  JsonObject getRubricsOfItem(String collectionId);
}
