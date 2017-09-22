package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import io.vertx.core.json.JsonObject;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IndexHandler {

  Logger LOGGER = LoggerFactory.getLogger(IndexHandler.class);

  void indexDocument(String entityId) throws Exception;

  void indexDocuments(JsonObject idsJson) throws Exception;

  void deleteIndexedDocument(String documentId) throws Exception;

  void increaseCount(String entityId, String field) throws Exception;

  void decreaseCount(String entityId, String field) throws Exception;

  void updateCount(String entityId, String field, int count) throws Exception;

  void updateViewCount(String entityId, Long viewCount);
  
  void updateUserDocuments(String userId) throws Exception;

  void indexEnhancedKeywords(String id, Map<String, Object> sourceAsMap) throws Exception;
}
