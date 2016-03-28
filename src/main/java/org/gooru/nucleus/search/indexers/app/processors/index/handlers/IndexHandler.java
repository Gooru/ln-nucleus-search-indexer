package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public interface IndexHandler {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(IndexHandler.class);

	public void indexDocument(String entityId) throws Exception;
	
	public void indexDocuments(JsonObject idsJson) throws Exception;

	public void deleteIndexedDocument(String documentId) throws Exception;
	
	public void indexDocmentPartial(JsonObject json) throws Exception;
}
