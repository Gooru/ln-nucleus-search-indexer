package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public interface IndexHandler {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(IndexHandler.class);

	public void indexDocument(String entityId) throws Exception;
	
	public void indexDocuments(JsonObject idsJson) throws Exception;

	public void deleteIndexedDocument(String documentId) throws Exception;
	
	public void increaseCount(String entityId, String field) throws Exception;
	
	public void decreaseCount(String entityId, String field) throws Exception;
	
	public void updateCount(String entityId, String field, int count) throws Exception;

	public void updateViewCount(String entityId, Long viewCount) throws Exception;
}
