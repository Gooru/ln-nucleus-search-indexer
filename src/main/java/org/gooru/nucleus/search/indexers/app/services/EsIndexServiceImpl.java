package org.gooru.nucleus.search.indexers.app.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.gooru.nucleus.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchRegistry;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEio;
import org.gooru.nucleus.search.indexers.app.index.model.ContentInfoEio;
import org.gooru.nucleus.search.indexers.app.index.model.ResourceInfoEo;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.BaseUtil;
import org.gooru.nucleus.search.indexers.app.utils.IdIterator;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.IndexScriptBuilder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 */
public class EsIndexServiceImpl implements IndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(EsIndexServiceImpl.class);

  private static String getIndexByType(String type) {
    String indexName = null;
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_ASSESSMENT) || type.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
      indexName = IndexNameHolder.getIndexName(EsIndex.COLLECTION);
    }
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_QUESTION) || type.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
      indexName = IndexNameHolder.getIndexName(EsIndex.RESOURCE);
    }
    return indexName;
  }

  private static String  getIndexTypeByType(String type) {
    String indexType = null;
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_ASSESSMENT) || type.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
      indexType = IndexerConstants.TYPE_COLLECTION;
    }
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_QUESTION) || type.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
      indexType = IndexerConstants.TYPE_RESOURCE;
    }
    return indexType;
  }

  private static int getInteger(Object value) {
    return value == null ? 0 : (int) value;
  }

  private static long getLong(Object value) {
    long views = 0L;
    if (value != null) {
      if (value instanceof Integer) {
        views = (long) (int) value;
      } else if (value instanceof Long) {
        views = (long) value;
      }

    }
    return views;
  }

  private static String getExectueOperation(String type) {
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
      return ExecuteOperationConstants.GET_COLLECTION;
    } else if (type.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
      return ExecuteOperationConstants.GET_RESOURCE;
    }
    return null;

  }

  @Override
  public void deleteDocuments(String indexableIds, String indexName, String type) {
    for (String key : indexableIds.split(",")) {
      getClient().prepareDelete(indexName, type, key).execute().actionGet();
    }
  }

  @Override
  public void indexDocuments(String indexableIds, String indexName, String typeName, JsonObject body) throws Exception {

    new IdIterator(indexableIds) {
      @Override
      public void execute(String indexableId) throws Exception {
        if ((typeName.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE) || typeName.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION))) {
          if (!body.isEmpty()) {
            try {
              // Get statistics and extracted text data from backup index
              Map<String, Object> contentInfoAsMap =
                      getDocument(indexableId, IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO);
              Map<String, Object> statisticsAsMap = null;
              Map<String, Object> resourceInfoAsMap = null;
              if (contentInfoAsMap != null) {
                statisticsAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.STATISTICS);
                LOGGER.debug("statistics index data : " + statisticsAsMap);
                body.put("isBuildIndex", true);
                if (BaseUtil.isNotNull(contentInfoAsMap, IndexerConstants.RESOURCE_INFO)) {
                  resourceInfoAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.RESOURCE_INFO);
                }
              }
              setExistingStatisticsData(body, statisticsAsMap, typeName);
              setResourceInfoData(body, resourceInfoAsMap, typeName);
              
              getClient().prepareIndex(indexName, typeName, indexableId).setSource(EsIndexSrcBuilder.get(typeName).buildSource(body)).execute()
                      .actionGet();
            } catch (Exception e) {
              LOGGER.info("Exception while indexing");
              throw new Exception(e);
            }
          }
        }
      }
    };
  }

  @Override
  public void refreshIndex(String indexName) {
    getClient().admin().indices().refresh(new RefreshRequest(indexName));
  }

  @Override
  public Map<String, Object> getDocument(String id, String indexName, String type) {
    GetResponse response = getClient().prepareGet(indexName, type, id).execute().actionGet();
    return response != null ? response.getSource() : null;
  }

  @Override
  public void indexDocumentByFields(String id, String indexName, String typeName, Map<String, Object> fieldValues) throws Exception {
    try {
      Map<String, Object> paramsField = new HashMap<>();
      StringBuffer scriptQuery = new StringBuffer();
      IndexScriptBuilder.buildScript(id, paramsField, scriptQuery, fieldValues);
      LOGGER.debug("Index name : " + indexName + " type : " + typeName + " id :" + id);
      LOGGER.debug("Index update script : " + scriptQuery.toString());
      getClient().prepareUpdate(indexName, typeName, id).setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField))
                 .execute().actionGet();

      //Update Statistics Index
      getClient().prepareUpdate(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id)
                 .setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField)).execute().actionGet();
    } catch (Exception e) {
      LOGGER.error("Update documentByFields Failed!, Exception : " + e);
      throw new Exception(e);
    }
  }

  @Override
  public void buildIndex(String idString, String typeName) throws Exception {
    String indexName = getIndexByType(typeName);
    new IdIterator(idString) {

      @SuppressWarnings("unchecked")
      @Override
      public void execute(String indexableId) throws Exception {
        try {
          // Fetch data from DB for given content Id
          ProcessorContext context = new ProcessorContext(indexableId, getExectueOperation(typeName));
          JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
          ValidationUtil.rejectIfNull(result, "DB return null data for id " + indexableId);
          // Get statistics and extracted text data from backup index
          Map<String, Object> contentInfoAsMap =
                  getDocument(indexableId, IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO);
          if (contentInfoAsMap != null) {
              Map<String, Object> statisticsAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.STATISTICS);
              LOGGER.debug("statistics index data : " + statisticsAsMap);
              result.put("isBuildIndex", true);
              setExistingStatisticsData(result, statisticsAsMap, typeName);
            if (BaseUtil.isNotNull(contentInfoAsMap, IndexerConstants.RESOURCE_INFO)) {
              Map<String, Object> resourceInfoAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.RESOURCE_INFO);
              setResourceInfoData(result, resourceInfoAsMap, typeName);
            }
          }
          LOGGER.debug("index source data : " + result.toString());

          getClient().prepareIndex(indexName, typeName, indexableId).setSource(EsIndexSrcBuilder.get(typeName).buildSource(result)).execute()
                  .actionGet();
          LOGGER.debug("EISI->indexDocument : Indexed " + typeName + " id  : " + indexableId);
        } catch (Exception ex) {
          LOGGER.error("EISI->Re-index failed for " + typeName + " id : " + indexableId + " Exception ", ex);
          INDEX_FAILURES_LOGGER.error(" buildIndex() : Failed : " + typeName + " id : " + indexableId);
          throw new Exception(ex);
        }
      }
    };

  }

  private Client getClient() {
    return ElasticSearchRegistry.getFactory().getClient();
  }

  private void setExistingStatisticsData(JsonObject source, Map<String, Object> statisticsMap, String typeName) {
    long viewsCount = 0L;
    int collabCount = 0;
    int remixCount = 0;

    if (statisticsMap != null) {
      if (typeName.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
        viewsCount = getLong(statisticsMap.get(ScoreConstants.VIEW_COUNT));
      }
      if (typeName.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
        viewsCount = getLong(statisticsMap.get(ScoreConstants.VIEW_COUNT));
        collabCount = getInteger(statisticsMap.get(ScoreConstants.COLLAB_COUNT));
        remixCount = getInteger(statisticsMap.get(ScoreConstants.COLLECTION_REMIX_COUNT));
      }
    }

    source.put(ScoreConstants.VIEW_COUNT, viewsCount);
    source.put(ScoreConstants.COLLAB_COUNT, collabCount);
    source.put(ScoreConstants.COLLECTION_REMIX_COUNT, remixCount);
  }
  
  private void setResourceInfoData(JsonObject source, Map<String, Object> resourceInfoMap, String typeName) {
    String text = null;
    if (resourceInfoMap != null) {
      if (typeName.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE) && BaseUtil.isNotNull(resourceInfoMap, IndexerConstants.TEXT)) {
        text = resourceInfoMap.get(IndexerConstants.TEXT).toString().trim();
      }
    }
    source.put(IndexerConstants.TEXT, text);
  }

  @Override
  public void bulkIndexStatisticsField(JsonArray jsonArr) {
    try{
      BulkRequestBuilder bulkRequest = getClient().prepareBulk();
      Iterator<Object> iter = jsonArr.iterator();
      LOGGER.debug("Batch size : " + jsonArr.size());
      while(iter.hasNext()){
        JsonObject data = (JsonObject) iter.next();
        if(data != null){
          LOGGER.debug("received index message from insights : " + data.toString());
          Map<String, Object> paramsField = new HashMap<>();
          StringBuffer scriptQuery = new StringBuffer();
          Map<String, Object> fieldValues = new HashMap<>();
          
          fieldValues.put(ScoreConstants.VIEWS_COUNT_FIELD, data.getLong(EventsConstants.EVT_DATA_VIEW_COUNT));
          IndexScriptBuilder.buildScript(data.getString(EventsConstants.EVT_DATA_ID), paramsField, scriptQuery, fieldValues);
          LOGGER.debug("script : " + scriptQuery.toString());
          LOGGER.debug("param fields : " + paramsField.toString());
          bulkRequest.add(getClient().prepareUpdate(getIndexByType(data.getString(EventsConstants.EVT_DATA_TYPE)), getIndexTypeByType(data.getString(EventsConstants.EVT_DATA_TYPE)), data.getString(EventsConstants.EVT_DATA_ID)).setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField)));
         // update to content info index 
          bulkRequest.add(getClient().prepareUpdate(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, data.getString(EventsConstants.EVT_DATA_ID)).setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField)));
        }
      }
      BulkResponse bulkResponse = bulkRequest.execute().actionGet();
      if(bulkResponse.hasFailures()){
        BulkItemResponse[] responses =  bulkResponse.getItems();
        for(BulkItemResponse response : responses){
          if(response.isFailed()){
            INDEX_FAILURES_LOGGER.error(" bulkIndexStatisticsField() : Failed  id : " + response.getId() + " Exception "+response.getFailureMessage());
          }
        }
        throw new Exception(bulkResponse.buildFailureMessage());
      }
      else {
        LOGGER.debug("Successfully updated view count bulk");
      }
    }
    catch(Exception e){
      LOGGER.error("Failed to update statistics fields ", e.getMessage());
    }
  }

  @Override
  public void bulkIndexDocuments(JsonArray jsonArr, String indexType, String index) {
    try{
      int batchIndex = 0;
      boolean contIndex = true;
      int batchSize = jsonArr.size();

      while(contIndex){
        BulkRequestBuilder bulkRequest = getClient().prepareBulk();
        for(int bulkReqIndex=batchIndex; bulkReqIndex < 50; bulkReqIndex++){
          JsonObject data = jsonArr.getJsonObject(bulkReqIndex);
          if(data != null){
            bulkRequest.add(getClient().prepareIndex(index, indexType, data.getString("id")).setSource(data));
          }
          batchIndex++;
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if(bulkResponse.hasFailures()){
          BulkItemResponse[] responses =  bulkResponse.getItems();
          for(BulkItemResponse response : responses){
            if(response.isFailed()){
              INDEX_FAILURES_LOGGER.error(" bulkIndexDocuments() : Failed  id : " + response.getId());
            }
          }
          throw new Exception(bulkResponse.buildFailureMessage());
        }
      }
      
      if(batchIndex >= batchSize){
        contIndex = false;
      }
    }
    catch(Exception e){
      LOGGER.error("EIS->bulkIndexDocuments : Failed", e);
    }
  }
  
  public void buildInfoIndex(String id) {
    JsonObject inputJson = getContentFromRepo(id);
    if (inputJson != null && !inputJson.isEmpty()) {
      String url = inputJson.getString(EntityAttributeConstants.URL, null);
      String contentFormat = inputJson.getString(EntityAttributeConstants.CONTENT_FORMAT);
      
      if (url != null) {
        //Extract text from URL
        long extractionStartTime = System.currentTimeMillis();
        String text = CrawlerService.instance().extractUrl(url);
        LOGGER.info("Time to extract url for id : {} is {}ms ", (System.currentTimeMillis() - extractionStartTime), id);

        //Build contentInfo index source
        JsonObject contentInfoJson = buildContentInfoEsIndexSrc(id, contentFormat, text);
        
        //Index text when available
        if (contentInfoJson != null) {
          try {
            getClient().prepareIndex(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id)
                    .setSource(contentInfoJson.toString()).execute();
            indexDocuments(id, IndexNameHolder.getIndexName(EsIndex.RESOURCE), IndexerConstants.TYPE_RESOURCE, inputJson);
          } catch (Exception e) {
            LOGGER.error("Text Extraction : Indexing failed for id : " + id + " Exception ", e.getMessage());
          }
        } else {
          LOGGER.info("Text Extraction : Extracted text is null for id {}", id);
        }
        LOGGER.info("Total time to extract url for id : {} is {}ms ", id, (System.currentTimeMillis() - extractionStartTime));
      }
    }
  }

  private JsonObject getContentFromRepo(String id) {
    ProcessorContext context = new ProcessorContext(id, ExecuteOperationConstants.GET_RESOURCE);
    JsonObject inputJson = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    ValidationUtil.rejectIfNull(inputJson, ErrorMsgConstants.RESOURCE_DATA_NULL);
    LOGGER.debug("EISI->indexDocument: getIndexDataContent() returned:" + inputJson);
    return inputJson;
  }

  private JsonObject buildContentInfoEsIndexSrc(String id, String contentFormat, String text) {
    if (StringUtils.isNotBlank(text)) {
      ContentInfoEio contentInfoEo = new ContentInfoEio();
      contentInfoEo.setId(id);
      contentInfoEo.setContentFormat(contentFormat);
      contentInfoEo.setIndexUpdatedTime(new Date(System.currentTimeMillis()));
      
      ResourceInfoEo resourceInfo = new ResourceInfoEo();
      resourceInfo.setText(text.trim());
      contentInfoEo.setResourceInfo(resourceInfo.getResourceInfo());

      Map<String, Object> contentInfoAsMap = getDocument(id, IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO);
      contentInfoEo.setStatistics(buildStatisticsData(contentFormat, contentInfoAsMap));
      return contentInfoEo.getContentInfoJson();
    }
    return null;
  }

  private JsonObject buildStatisticsData(String contentFormat, Map<String, Object> contentInfoAsMap) {
    Map<String, Object> statisticsAsMap = null;
    if (contentInfoAsMap != null) {
      statisticsAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.STATISTICS);
    }
    long viewsCount = 0L;
    int collaboratorCount = 0;
    int remixCount = 0;

    if (statisticsAsMap != null) {
      if (contentFormat != null && contentFormat.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
        viewsCount = getLong(statisticsAsMap.get(ScoreConstants.VIEW_COUNT));
      }
      if (contentFormat != null && contentFormat.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
        viewsCount = getLong(statisticsAsMap.get(ScoreConstants.VIEW_COUNT));
        collaboratorCount = getInteger(statisticsAsMap.get(ScoreConstants.COLLAB_COUNT));
        remixCount = getInteger(statisticsAsMap.get(ScoreConstants.COLLECTION_REMIX_COUNT));
      }
    }
    StatisticsEo statisticEo = new StatisticsEo();
    statisticEo.setViewsCount(viewsCount);
    statisticEo.setCollaboratorCount(collaboratorCount);
    statisticEo.setCollectionRemixCount(remixCount);
    return statisticEo.getStatistics();
  }
  
  public void buildInfoIndex(String id, JsonObject source) {
    if (source != null && !source.isEmpty()) {
      String url = source.getString(EntityAttributeConstants.URL, null);
      String contentFormat = source.getString(EntityAttributeConstants.CONTENT_FORMAT);
      if (url != null) {
        String text = CrawlerService.instance().extractUrl(url);
        JsonObject contentInfoJson = buildContentInfoEsIndexSrc(id, contentFormat, text);
        if (text.trim() != null) {
          try {
            getClient().prepareIndex(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id)
                    .setSource(contentInfoJson.toString()).execute();
          } catch (Exception e) {
            LOGGER.error("Text Extraction : Re-index failed for " + contentFormat + " id : " + id + " Exception ", e.getMessage());
          }
        } else {
          LOGGER.info("Text Extraction : Extracted text is null for id {}", id);
        }
      } else {
        LOGGER.info("Text Extraction : Url field is null to extract for id {}", id);
      }
    } else {
      LOGGER.info("Text Extraction : Input Jsonobject is null to extract url for id {}", id);
    }
  }
}
