package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface RubricRepository {

  static RubricRepository instance() {
    return new RubricRepositoryImpl();
  }

  JsonObject getRubric(String rubricId);

  JsonObject getDeletedRubric(String rubricId);
}
