package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

public interface ContentRepository {

  static ContentRepository instance() {
    return new ContentRepositoryImpl();
  }

  JsonObject getResource(String contentID);

  JsonObject getQuestion(String contentID);

  JsonObject getContentByType(String contentId, String contentFormat);

  List<Map> getCollectionMeta(String parentContentId);

  JsonObject getQuestionAndOriginalResourceIds(String collectionId);
  
  JsonObject getDeletedContent(String contentId);
  
  JsonObject getUserQuestions(String userId);

  JsonObject getQuestionById(String contentId);

}
