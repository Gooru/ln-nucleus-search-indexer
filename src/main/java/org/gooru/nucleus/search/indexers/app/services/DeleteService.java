/**
 *
 */
package org.gooru.nucleus.search.indexers.app.services;

import io.vertx.core.json.JsonArray;

/**
 * @author Renuka
 */
public interface DeleteService {

  static DeleteService instance() {
    return new EsDeleteServiceImpl();
  }

  /**
   * Delete content in index using the entry id
   * @throws Exception 
   */
  void deleteDocuments(String idString, String indexName, String typeName) throws Exception;

  void deleteDocuments(String key, String type) throws Exception;

  void bulkDeleteDocuments(JsonArray jsonArr, String indexType, String index);

  void trackDeletes(String key, String type);
  
}
