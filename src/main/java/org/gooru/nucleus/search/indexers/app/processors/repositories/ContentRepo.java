package org.gooru.nucleus.search.indexers.app.processors.repositories;

import io.vertx.core.json.JsonObject;


public interface ContentRepo {
  JsonObject getResource(String contentID);

  JsonObject getDeletedResource(String contentID);

  JsonObject getQuestion(String contentID);

  JsonObject getDeletedQuestion(String contentID);

}
