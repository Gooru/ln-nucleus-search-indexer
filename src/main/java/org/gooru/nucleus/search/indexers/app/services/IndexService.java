/**
 * 
 */
package org.gooru.nucleus.search.indexers.app.services;

import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 * 
 */
public interface IndexService {

	/**
	 * Delete content in index using the entry id
	 * 
	 */
	void deleteDocuments(String idString, String indexName, String typeName);

	/**
	 * Index a single content based on inputs.
	 * 
	 */
	void indexDocuments(String idString, String indexName, String typeName, JsonObject body);

	/**
	 * Refresh the index
	 * 
	 */
	void refreshIndex(String indexName);

}
