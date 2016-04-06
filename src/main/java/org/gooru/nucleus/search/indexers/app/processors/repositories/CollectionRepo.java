package org.gooru.nucleus.search.indexers.app.processors.repositories;

import io.vertx.core.json.JsonObject;

public interface CollectionRepo {
  JsonObject getCollection(String contentID);

  JsonObject getDeletedCollection(String contentID);

  JsonObject getAssessment(String contentID);

  JsonObject getDeletedAssessment(String contentID);

}
