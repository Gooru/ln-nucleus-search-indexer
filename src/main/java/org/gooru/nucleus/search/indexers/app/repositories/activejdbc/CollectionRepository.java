package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.javalite.activejdbc.LazyList;

public interface CollectionRepository {

  static CollectionRepository instance() {
    return new CollectionRepositoryImpl();
  }

  JsonObject getCollection(String contentID);

  JsonObject getAssessment(String contentID);

  JsonObject getCollectionByType(String contentID, String format);

  @SuppressWarnings("rawtypes")
  List<Map> getContentsOfCollection(String collectionId);

  JsonObject getDeletedCollection(String collectionId);
  
  JsonObject getUserCollections(String userId);

  LazyList<Collection> getCollectionsByLessonId(String lessonId);

  LazyList<Collection> getCollectionsByUnitId(String unitId);

  LazyList<Collection> getCollectionsByCourseId(String courseId);
}
