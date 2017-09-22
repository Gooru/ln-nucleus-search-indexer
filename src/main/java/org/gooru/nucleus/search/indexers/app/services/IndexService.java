/**
 *
 */
package org.gooru.nucleus.search.indexers.app.services;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 */
public interface IndexService {

  static IndexService instance() {
    return new EsIndexServiceImpl();
  }

  /**
   * Delete content in index using the entry id
   * @throws Exception 
   */
  void deleteDocuments(String idString, String indexName, String typeName) throws Exception;

  /**
   * Index a single content based on inputs.
   *
   * @throws Exception
   */
  void indexDocuments(String idString, String indexName, String typeName, JsonObject body) throws Exception;

  Map<String, Object> getDocument(String id, String indexName, String type);

  void indexDocumentByFields(String id, String indexName, String typeName, Map<String, Object> fieldValues) throws Exception;

  void buildIndex(String idString, String typeName) throws Exception;
  
  void bulkIndexStatisticsField(JsonArray jsonArr);

  void bulkIndexDocuments(JsonArray jsonArr, String indexType, String index);
  
  void buildInfoIndex(String idString, String contentFormat) throws Exception;
    
  void updateBrokenStatus(String ids, boolean isUpdateBroken) throws Exception;

  void deleteDocuments(String key, String type) throws Exception;

  void indexDocumentByField(String id, String indexName, String typeName, Map<String, Object> fieldValues, Map<String, Object> contentInfoSource) throws Exception;

  SearchResponse getDocument(String indexName, String type, SearchSourceBuilder boolQuery) throws Exception;
  
}
