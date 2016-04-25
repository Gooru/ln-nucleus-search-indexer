/**
 *
 */
package org.gooru.nucleus.search.indexers.app.services;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * @author Renuka
 */
public interface IndexService {

  static IndexService instance() {
    return new EsIndexServiceImpl();
  }

  /**
   * Delete content in index using the entry id
   */
  void deleteDocuments(String idString, String indexName, String typeName);

  /**
   * Index a single content based on inputs.
   *
   * @throws Exception
   */
  void indexDocuments(String idString, String indexName, String typeName, JsonObject body) throws Exception;

  /**
   * Refresh the index
   */
  void refreshIndex(String indexName);

  Map<String, Object> getDocument(String id, String indexName, String type);

  void indexDocumentByFields(String id, String indexName, String typeName, Map<String, Object> fieldValues) throws Exception;

  void buildIndex(String idString, String typeName) throws Exception;
  
  void bulkIndexStatisticsField(JsonArray jsonArr);

  void bulkIndexDocuments(JsonArray jsonArr, String indexType, String index);
  
}
