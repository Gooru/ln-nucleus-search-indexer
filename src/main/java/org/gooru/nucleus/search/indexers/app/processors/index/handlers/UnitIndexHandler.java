package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.UnitIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class UnitIndexHandler extends BaseIndexHandler implements IndexHandler {

  private final String indexName;
  
  public UnitIndexHandler() {
    this.indexName = getIndexName();
  }
  
  @Override
  public void indexDocument(String unitId) throws Exception {
    try {
      ProcessorContext context = new ProcessorContext(unitId, ExecuteOperationConstants.GET_UNIT);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNull(result, ErrorMsgConstants.UNIT_DATA_NULL);
      UnitIndexService.instance().indexDocument(unitId, result);
      LOGGER.debug("UIH->indexDocument : Indexed unit id : " + unitId);
    } catch (Exception ex) {
      LOGGER.error("UIH->Re-index failed for unit : " + unitId + " Exception " + ex);
      throw new Exception(ex);
    }
  }

  @Override
  public void indexDocuments(JsonObject idsJson) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteIndexedDocument(String unitId) throws Exception {
    try{
      LOGGER.debug("URIH->deleteIndexedDocument : Processing delete unit for id : " + unitId);
      ProcessorContext context = new ProcessorContext(unitId, ExecuteOperationConstants.GET_DELETED_UNIT);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.UNIT_NOT_DELETED);
      UnitIndexService.instance().deleteDocument(unitId);
    }
    catch(Exception e){
      LOGGER.error("URIH-> Delete failed for unit : " + unitId + " Exception " + e);
      throw new Exception(e);
    }
  }

  @Override
  public void increaseCount(String unitId, String field) throws Exception {
    
  }

  @Override
  public void decreaseCount(String entityId, String field) throws Exception {
    
  }

  @Override
  public void updateCount(String entityId, String field, int count) throws Exception {
    
  }

  @Override
  public void updateViewCount(String entityId, Long viewCount) {
    
  }

  @Override
  public void updateUserDocuments(String userId) throws Exception {
    
  }
  
  @Override
  public void indexEnhancedKeywords(String id, Map<String, Object> sourceAsMap) throws Exception {
    
  }

  private String getIndexName() {
    return IndexNameHolder.getIndexName(EsIndex.UNIT);
  }

}
