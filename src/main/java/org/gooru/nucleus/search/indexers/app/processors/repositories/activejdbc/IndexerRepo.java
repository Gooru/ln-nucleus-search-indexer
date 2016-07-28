package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

public interface IndexerRepo {

  JsonObject getIndexDataContent();

  JsonObject getIndexDataCollection();

  JsonObject getAssessment();

}
