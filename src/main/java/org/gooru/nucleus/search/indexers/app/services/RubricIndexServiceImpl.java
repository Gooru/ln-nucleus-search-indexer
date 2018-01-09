package org.gooru.nucleus.search.indexers.app.services;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.gooru.nucleus.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class RubricIndexServiceImpl extends BaseIndexService implements RubricIndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(RubricIndexServiceImpl.class);
  
  @Override
  public void indexDocument(String id, JsonObject data) throws Exception {
    if (!data.isEmpty()) {
      try {        
        IndexRequest request = new IndexRequest(getIndexName(), IndexerConstants.TYPE_RUBRIC, id).source(EsIndexSrcBuilder.get(IndexerConstants.TYPE_RUBRIC).buildSource(data), XContentType.JSON); 
        getHighLevelClient().index(request);
      } catch (Exception e) {
          LOGGER.info("Exception while indexing rubric");
          throw new Exception(e);
      }
    }
  }
  
  @Override
  public void deleteDocument(String id) throws Exception {
    try {
      DeleteRequest delete = new DeleteRequest(getIndexName(), IndexerConstants.TYPE_RUBRIC, id); 
      getHighLevelClient().delete(delete);
    }
    catch(Exception e){
      LOGGER.error("Failed to delete rubric from index");
      throw new Exception(e);
    }
  }
  
  @Override
  public void deleteIndexedRubric(String key, String type) throws Exception {
    try {
      LOGGER.debug("RubISI->deleteIndexedRubric : Processing delete rubric for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_RUBRIC);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.RUBRIC_UNAVAILABLE);
      IndexService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception ex) {
      LOGGER.error("RUBISI->deleteIndexedRubric : Delete from index failed for rubric id : " + key + " Exception : " + ex);
      throw new Exception(ex);
    }
  }
  
  
  private String getIndexName(){
    return IndexNameHolder.getIndexName(EsIndex.RUBRIC);
  }
}
