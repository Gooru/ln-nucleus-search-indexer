package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

public interface CollectionRepository {

  static CollectionRepository instance() {
    return new CollectionRepositoryImpl();
  }

  JsonObject getCollection(String contentID);

  JsonObject getAssessment(String contentID);

  JsonObject getCollectionByType(String contentID, String format);

  List<Map> getContentsOfCollection(String collectionId);

  JsonObject getDeletedCollection(String collectionId);
}
