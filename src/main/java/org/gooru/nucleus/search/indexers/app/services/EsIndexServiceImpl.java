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
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.gooru.nucleus.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ContentInfoEio;
import org.gooru.nucleus.search.indexers.app.index.model.ResourceInfoEo;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.BadRequestException;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
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
public class EsIndexServiceImpl extends BaseIndexService implements IndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(EsIndexServiceImpl.class);

  public static String getIndexByType(String type) {
    switch (type) {
    case IndexerConstants.TYPE_ASSESSMENT:
    case IndexerConstants.TYPE_COLLECTION:
      return IndexNameHolder.getIndexName(EsIndex.COLLECTION);
    case IndexerConstants.TYPE_QUESTION:
    case IndexerConstants.TYPE_RESOURCE:
      return IndexNameHolder.getIndexName(EsIndex.RESOURCE);
    case IndexerConstants.TYPE_COURSE:
      return IndexNameHolder.getIndexName(EsIndex.COURSE);
    case IndexerConstants.TYPE_CROSSWALK:
      return IndexNameHolder.getIndexName(EsIndex.CROSSWALK);
    case IndexerConstants.TYPE_UNIT:
      return IndexNameHolder.getIndexName(EsIndex.UNIT);
    case IndexerConstants.TYPE_LESSON:
      return IndexNameHolder.getIndexName(EsIndex.LESSON);
    case IndexerConstants.TYPE_RUBRIC:
      return IndexNameHolder.getIndexName(EsIndex.RUBRIC);
    case IndexerConstants.TYPE_TAXONOMY:
      return IndexNameHolder.getIndexName(EsIndex.TAXONOMY);
    default:
      return null;
    }
  }

  public static String getIndexTypeByType(String type) {
    switch (type) {
    case IndexerConstants.TYPE_ASSESSMENT:
    case IndexerConstants.TYPE_COLLECTION:
      return IndexerConstants.TYPE_COLLECTION;
    case IndexerConstants.TYPE_QUESTION:
    case IndexerConstants.TYPE_RESOURCE:
      return IndexerConstants.TYPE_RESOURCE;
    case IndexerConstants.TYPE_COURSE:
      return IndexerConstants.TYPE_COURSE;
    case IndexerConstants.TYPE_CROSSWALK:
      return IndexerConstants.TYPE_CROSSWALK;
    case IndexerConstants.TYPE_UNIT:
      return IndexerConstants.TYPE_UNIT;
    case IndexerConstants.TYPE_LESSON:
      return IndexerConstants.TYPE_LESSON;
    case IndexerConstants.TYPE_RUBRIC:
      return IndexerConstants.TYPE_RUBRIC;
    case IndexerConstants.TYPE_TAXONOMY:
      return IndexerConstants.TYPE_TAXONOMY;
    default:
      return null;
    }
  }

  private static String getExecuteOperation(String type) {
    switch (type) {
    case IndexerConstants.TYPE_RESOURCE:
      return ExecuteOperationConstants.GET_RESOURCE;
    case IndexerConstants.TYPE_QUESTION:
      return ExecuteOperationConstants.GET_QUESTION;
    case IndexerConstants.TYPE_COLLECTION:
      return ExecuteOperationConstants.GET_COLLECTION;
    case IndexerConstants.TYPE_COURSE:
      return ExecuteOperationConstants.GET_COURSE;
    case IndexerConstants.TYPE_CROSSWALK:
      return ExecuteOperationConstants.GET_CROSSWALK;
    case IndexerConstants.TYPE_UNIT:
      return ExecuteOperationConstants.GET_UNIT;
    case IndexerConstants.TYPE_LESSON:
      return ExecuteOperationConstants.GET_LESSON;
    case IndexerConstants.TYPE_RUBRIC:
      return ExecuteOperationConstants.GET_RUBRIC;
    case IndexerConstants.TYPE_TAXONOMY:
      return ExecuteOperationConstants.GET_TAXONOMY_CODE;
    default:
      return null;
    }
  }

  @Override
  public void deleteDocuments(String indexableIds, String indexName, String type) {
    for (String key : indexableIds.split(",")) {
      getClient().prepareDelete(indexName, getIndexTypeByType(type), key).execute().actionGet();
      
      // Delete from CI index
      getClient().prepareDelete(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, key).execute().actionGet();
      
      trackDeletes(key, getIndexTypeByType(type));
    }
  }

  private void trackDeletes(String key, String type) {
    JsonObject deleteJson = new JsonObject();
    deleteJson.put(EntityAttributeConstants.GOORU_OID, key);
    deleteJson.put(EntityAttributeConstants.INDEX_TYPE, type);
    ProcessorContext context = null;
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
      context = new ProcessorContext(key, null, ExecuteOperationConstants.SAVE_DELETED_RESOURCE, null, deleteJson);
    } else if (type.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
      context = new ProcessorContext(key, null, ExecuteOperationConstants.SAVE_DELETED_COLLECTION, null, deleteJson);
    }
    if (context != null) {
      RepoBuilder.buildIndexerRepo(context).trackIndexActions();
    } else {
      LOGGER.info("Request Type : {}, We are currently tracking only resource and collection deletes", type);
    }
  }
  
  @Override
  public void deleteDocuments(String deletableIds, String type) throws Exception {
    new IdIterator(deletableIds) {
      @Override
      public void execute(String deletableId) throws Exception {
        switch (type) {
        case IndexerConstants.TYPE_RESOURCE:
          ResourceIndexService.instance().deleteIndexedResource(deletableId, type);
          break;
        case IndexerConstants.TYPE_QUESTION:
          ResourceIndexService.instance().deleteIndexedQuestion(deletableId, type);
          break;
        case IndexerConstants.TYPE_COLLECTION:
          CollectionIndexService.instance().deleteIndexedCollection(deletableId, type);
          break;
        case IndexerConstants.TYPE_COURSE:
          CourseIndexService.instance().deleteIndexedCourse(deletableId, type);
          break;
        case IndexerConstants.TYPE_UNIT:
          UnitIndexService.instance().deleteIndexedUnit(deletableId, type);
          break;
        case IndexerConstants.TYPE_LESSON:
          LessonIndexService.instance().deleteIndexedLesson(deletableId, type);
          break;
        case IndexerConstants.TYPE_RUBRIC:
          RubricIndexService.instance().deleteIndexedRubric(deletableId, type);
          break;
        case IndexerConstants.TYPE_CROSSWALK:
          CrosswalkIndexService.instance().deleteIndexedCrosswalk(deletableId, type);
          break;
        case IndexerConstants.TYPE_TAXONOMY:
          TaxonomyIndexService.instance().deleteIndexedTaxonomy(deletableId, type);
          break;
        default:
          LOGGER.error("Invalid type passed in, not able to delete");
          throw new InvalidRequestException("Invalid type : " + type);
        }
      }
    };
  }

  @Override
  public void indexDocuments(String indexableIds, String indexName, String typeName, JsonObject body) throws Exception {

    new IdIterator(indexableIds) {
      @Override
      public void execute(String indexableId) throws Exception {
        if ((IndexerConstants.RESOURCE_FORMATS.matcher(typeName).matches() || typeName.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION))) {
          if (!body.isEmpty()) {
            try {
              // Get statistics and extracted text data from backup index
              setResourceStasInfoData(body, indexableId, typeName);
              getClient().prepareIndex(indexName, getIndexTypeByType(typeName), indexableId).setSource(EsIndexSrcBuilder.get(typeName).buildSource(body)).execute()
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

  @SuppressWarnings("unchecked")
  private void setResourceStasInfoData(JsonObject data, String id, String typeName) throws Exception {
    try {
      Map<String, Object> contentInfoAsMap =
              getDocument(id, IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO);
      Map<String, Object> resourceInfoAsMap = null;
      if (contentInfoAsMap != null) {
        data.put("isBuildIndex", true);
        if (BaseUtil.isNotNull(contentInfoAsMap, IndexerConstants.RESOURCE_INFO)) {
          resourceInfoAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.RESOURCE_INFO);
        }
      }
      setExistingStatisticsData(data, contentInfoAsMap, typeName);
      setResourceInfoData(data, resourceInfoAsMap, typeName);
    }
    catch(Exception e){
      throw new Exception(e);
    }
  }
  
  @Override
  public void refreshIndex(String indexName) {
    getClient().admin().indices().refresh(new RefreshRequest(indexName));
  }

  @Override
  public Map<String, Object> getDocument(String id, String indexName, String type) {
    GetResponse response = null;
    try {
      response = getClient().prepareGet(indexName, getIndexTypeByType(type), id).execute().actionGet();
    } catch (Exception e) {
      LOGGER.info("Document not found in index for id : {}", id);
    }
    return (response != null && response.isExists()) ? response.getSource() : null;
  }
  
  @Override
  public SearchResponse getDocument(String indexName, String type, BoolQueryBuilder boolQuery) {
    SearchRequestBuilder requestBuilder = getClient().prepareSearch(indexName).setTypes(type).setQuery(boolQuery);
    SearchResponse result = requestBuilder.execute().actionGet();
    return result;
  }

  @Override
  public void indexDocumentByFields(String id, String indexName, String typeName, Map<String, Object> fieldValues) throws Exception {
    try {
      Map<String, Object> paramsField = new HashMap<>();
      StringBuffer scriptQuery = new StringBuffer();
      LOGGER.debug("incoming fields values: " + fieldValues.values().toString());

      IndexScriptBuilder.buildScript(id, paramsField, scriptQuery, fieldValues);
      LOGGER.debug("Index name : " + indexName + " type : " + typeName + " id :" + id);
      LOGGER.debug("Index update script : " + scriptQuery.toString());
      LOGGER.debug("Index params fields : " + paramsField.values().toString());
      getClient().prepareUpdate(indexName, getIndexTypeByType(typeName), id).setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField))
                 .execute().actionGet();

      //Update Statistics Index
      try{
        getClient().prepareUpdate(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id)
        .setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField)).execute().actionGet();
      }
      catch(Exception ex){
        if(ex instanceof DocumentMissingException){
          getClient().prepareIndex(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id)
          .setSource(buildContentInfoIndexSrc(id, typeName, fieldValues)).execute().actionGet();
        }else {
          throw new Exception(ex);
        }
      }
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
          ProcessorContext context = new ProcessorContext(indexableId, getExecuteOperation(typeName));
          JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
          ValidationUtil.rejectIfNotFound(result, "Invalid format type or DB returned null data for id " + indexableId);
          // Get statistics and extracted text data from backup index
          Map<String, Object> contentInfoAsMap =
                  getDocument(indexableId, IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO);
          switch(typeName) {
            case IndexerConstants.TYPE_RESOURCE :
            case IndexerConstants.TYPE_QUESTION :
            case IndexerConstants.TYPE_COLLECTION :
            case IndexerConstants.TYPE_ASSESSMENT :
              setExistingStatisticsData(result, contentInfoAsMap, typeName);
              result.put("isBuildIndex", true);
              break;
            case IndexerConstants.TYPE_COURSE : 
              CourseIndexService.instance().setExistingStatisticsData(result, contentInfoAsMap);
              break;
            case IndexerConstants.TYPE_UNIT :
              UnitIndexService.instance().setExistingStatisticsData(result, contentInfoAsMap);
              break;
            case IndexerConstants.TYPE_LESSON:
              LessonIndexService.instance().setExistingStatisticsData(result, contentInfoAsMap);
              break;
            case IndexerConstants.TYPE_RUBRIC:
              break;
            case IndexerConstants.TYPE_TAXONOMY:
            case IndexerConstants.TYPE_CROSSWALK:
              break;
            default:
              throw new BadRequestException("Invalid format type! Please pass valid format to index!");
          }

          if (contentInfoAsMap != null) {
            if (BaseUtil.isNotNull(contentInfoAsMap, IndexerConstants.RESOURCE_INFO)) {
              Map<String, Object> resourceInfoAsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.RESOURCE_INFO);
              setResourceInfoData(result, resourceInfoAsMap, typeName);
            }
          }

          // LOGGER.debug("index source data : " + result.toString());
          getClient().prepareIndex(indexName, getIndexTypeByType(typeName), indexableId).setSource(EsIndexSrcBuilder.get(typeName).buildSource(result)).execute()
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

  @Override
  public void indexDocumentByField(String id, String indexName, String typeName, Map<String, Object> contentSource, Map<String, Object> contentInfoSource) throws Exception {
    try {
      LOGGER.debug("Incoming fields : " + contentSource.toString());
      LOGGER.debug("Index name : " + indexName + " type : " + typeName + " id :" + id);
      updateContentIndex(id, indexName, typeName, contentSource, contentInfoSource);
      updateContentInfoIndex(id, typeName, contentSource, contentInfoSource);
    } catch (Exception e) {
      LOGGER.error("Update documentByField Failed!, Exception : " + e);
      throw new Exception(e);
    }
  }

  private void updateContentInfoIndex(String id, String typeName, Map<String, Object> contentSource, Map<String, Object> contentInfoSource)
          throws Exception {
    try {
      LOGGER.debug("Indexing: " + IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO) + " Index");
      UpdateRequest updateRequest = new UpdateRequest();
      updateRequest.index(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO)).type(IndexerConstants.TYPE_CONTENT_INFO).id(id).doc(contentInfoSource);
      UpdateResponse response = getClient().update(updateRequest).get();
      if (response.isCreated())
        LOGGER.info("Index " + IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO) + " : document updated!");

    } catch (Exception ex) {
      if (ex instanceof DocumentMissingException || ex.getCause().getCause().getCause() instanceof DocumentMissingException) {
        LOGGER.debug("Caught Document Missing Exception!!");
        IndexResponse createResponse = getClient().prepareIndex(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, id)
                .setSource(buildContentInfoIndexSrc(id, typeName, contentSource)).execute().actionGet();
        if (createResponse.isCreated())
          LOGGER.info("Index " + IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO) + " : document created!");

      } else {
        throw new Exception(ex);
      }
    }
  }

  private void updateContentIndex(String id, String indexName, String typeName, Map<String, Object> contentSource,
          Map<String, Object> contentInfoSource) throws Exception {
    try {
      LOGGER.debug("Indexing: " + indexName + " Index");
      UpdateRequest updateRequest = new UpdateRequest();
      updateRequest.index(indexName).type(typeName).id(id).doc(contentInfoSource);
      UpdateResponse response = getClient().update(updateRequest).get();
      if (response.isCreated())
        LOGGER.info("Index " + indexName + " : document updated!");

    } catch (Exception ex) {
      if (ex instanceof DocumentMissingException || ex.getCause().getCause().getCause() instanceof DocumentMissingException) {
        LOGGER.debug("Caught Document Missing Exception!");
        IndexResponse createResponse = getClient().prepareIndex(indexName, typeName, id).setSource(buildContentInfoIndexSrc(id, typeName, contentSource)).execute().actionGet();
        if (createResponse.isCreated())
          LOGGER.info("Index " + indexName + " : document created!");
      } else {
        throw new Exception(ex);
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  private void setExistingStatisticsData(JsonObject source, Map<String, Object> contentInfoAsMap, String typeName) {

    long viewsCount = 0L;
    int collabCount = 0;
    int remixCount = 0;

    if (contentInfoAsMap != null) {
      Map<String, Object> statisticsMap = (Map<String, Object>) contentInfoAsMap.get(IndexerConstants.STATISTICS);
      if(statisticsMap != null){
        LOGGER.debug("statistics index data : " + statisticsMap);
        if (IndexerConstants.RESOURCE_FORMATS.matcher(typeName).matches()) {
          viewsCount = getLong(statisticsMap.get(ScoreConstants.VIEW_COUNT));
        } else if (typeName.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
          viewsCount = getLong(statisticsMap.get(ScoreConstants.VIEW_COUNT));
          collabCount = getInteger(statisticsMap.get(ScoreConstants.COLLAB_COUNT));
          remixCount = getInteger(statisticsMap.get(ScoreConstants.COLLECTION_REMIX_COUNT));
        }
      }
    }

    source.put(ScoreConstants.VIEW_COUNT, viewsCount);
    source.put(ScoreConstants.COLLAB_COUNT, collabCount);
    source.put(ScoreConstants.COLLECTION_REMIX_COUNT, remixCount);
  }
  
  @SuppressWarnings("unchecked")
  private void setResourceInfoData(JsonObject source, Map<String, Object> resourceInfoMap, String typeName) {
    String text = null;
    JsonObject watsonTagsAsMap = null;
    if (resourceInfoMap != null) {
      if (IndexerConstants.RESOURCE_FORMATS.matcher(typeName).matches() && BaseUtil.isNotNull(resourceInfoMap, IndexerConstants.TEXT)) {
        text = resourceInfoMap.get(IndexerConstants.TEXT).toString().trim();
      } else if (BaseUtil.isNotNull(resourceInfoMap, IndexerConstants.WATSON_TAGS)) {
        watsonTagsAsMap = new JsonObject((Map<String, Object>) resourceInfoMap.get(IndexerConstants.WATSON_TAGS));
      }
    }
    source.put(IndexerConstants.TEXT, text);
    source.put(IndexerConstants.WATSON_TAGS, watsonTagsAsMap);
  }

  @Override
  public void bulkIndexStatisticsField(JsonArray jsonArr) {
    try {
      BulkRequestBuilder bulkRequest = getClient().prepareBulk();
      Iterator<Object> iter = jsonArr.iterator();
      LOGGER.debug("Batch size : " + jsonArr.size());
      Map<String, Map<String, Object>> viewsData = new HashMap<>();
      Map<String, String> contentType = new HashMap<>();
      while (iter.hasNext()) {
        JsonObject data = (JsonObject) iter.next();
        if (data != null) {
          LOGGER.debug("received index message from insights : " + data.toString());
          Map<String, Object> paramsField = new HashMap<>();
          StringBuffer scriptQuery = new StringBuffer();
          Map<String, Object> fieldValues = new HashMap<>();

          fieldValues.put(ScoreConstants.VIEWS_COUNT_FIELD, data.getLong(EventsConstants.EVT_DATA_VIEW_COUNT));
          IndexScriptBuilder.buildScript(data.getString(EventsConstants.EVT_DATA_ID), paramsField, scriptQuery, fieldValues);
          LOGGER.debug("script : " + scriptQuery.toString());
          LOGGER.debug("param fields : " + paramsField.toString());
          bulkRequest.add(getClient()
                  .prepareUpdate(getIndexByType(data.getString(EventsConstants.EVT_DATA_TYPE)),
                          getIndexTypeByType(data.getString(EventsConstants.EVT_DATA_TYPE)), data.getString(EventsConstants.EVT_DATA_ID))
                  .setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField)));
          // update to content info index
          bulkRequest.add(getClient()
                  .prepareUpdate(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO,
                          data.getString(EventsConstants.EVT_DATA_ID))
                  .setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField)));
          viewsData.put(data.getString(EventsConstants.EVT_DATA_ID), fieldValues);
          contentType.put(data.getString(EventsConstants.EVT_DATA_ID), data.getString(EventsConstants.EVT_DATA_TYPE));
        }
      }
      BulkResponse bulkResponse = bulkRequest.execute().actionGet();
      if (bulkResponse.hasFailures()) {
        BulkItemResponse[] responses = bulkResponse.getItems();
        for (BulkItemResponse response : responses) {
          if (response.isFailed()) {

            // If document missing in contentinfo index. Creating it.
            if (response.getFailure().getCause() instanceof DocumentMissingException && viewsData.containsKey(response.getId())) {
              getClient().prepareIndex(IndexNameHolder.getIndexName(EsIndex.CONTENT_INFO), IndexerConstants.TYPE_CONTENT_INFO, response.getId())
                      .setSource(buildContentInfoIndexSrc(response.getId(), contentType.get(response.getId()), viewsData.get(response.getId())))
                      .execute().actionGet();
            } else {
              INDEX_FAILURES_LOGGER
                      .error(" bulkIndexStatisticsField() : Failed  id : " + response.getId() + " Exception " + response.getFailureMessage());
            }

          }
        }
        throw new Exception(bulkResponse.buildFailureMessage());
      } else {
        LOGGER.debug("Successfully updated view count bulk");
      }
    }
    catch(Exception e){
      LOGGER.error("Failed to update statistics fields ", e.getMessage());
    }
  }

  @Override
  public void bulkIndexDocuments(JsonArray jsonArr, String contentType, String index) {
    try{
      
      if(!IndexerConstants.RESOURCE_FORMATS.matcher(contentType).matches() || !contentType.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)){
        throw new Exception("Invalid type in bulkIndexDocuments() !");
      }
      
      int batchIndex = 0;
      boolean contIndex = true;
      int batchSize = jsonArr.size();
      
      LOGGER.debug("bulkIndexDocuments()-> content type : "+contentType +" total batch size : " + batchSize);
      while(contIndex){
        BulkRequestBuilder bulkRequest = getClient().prepareBulk();
        for(int bulkReqIndex=batchIndex; bulkReqIndex < 50; bulkReqIndex++){
          JsonObject data = jsonArr.getJsonObject(bulkReqIndex);
          if(data != null){
            // Get statistics and extracted text data from backup index
            setResourceStasInfoData(data, data.getString(EntityAttributeConstants.ID), contentType);
            
            bulkRequest.add(getClient().prepareIndex(index, getIndexTypeByType(contentType), data.getString(EntityAttributeConstants.ID)).setSource(EsIndexSrcBuilder.get(contentType).buildSource(data)));
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
        else {
          LOGGER.debug("bulkIndexDocuments() -> Successfully Completed for batch size : 50 !");
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
  
  @Override
  public void buildInfoIndex(String id, String contentFormat) throws Exception {
    JsonObject inputJson = getContentFromRepo(id, contentFormat);
    if (inputJson != null && !inputJson.isEmpty()) {
      String url = inputJson.getString(EntityAttributeConstants.URL, null);
      
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
            
            // Get statistics and extracted text data from backup index
            setResourceStasInfoData(inputJson, id, IndexerConstants.TYPE_RESOURCE);
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

  private JsonObject getContentFromRepo(String id, String contentType) throws Exception {
    ProcessorContext context = new ProcessorContext(id, getExecuteOperation(contentType));
    JsonObject inputJson = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
    ValidationUtil.rejectIfNotFound(inputJson, ErrorMsgConstants.ORIGINAL_RESOURCE_DATA_NULL);
    LOGGER.debug("EISI->indexDocument: getIndexDataContent() returned:" + inputJson);
    return inputJson;
  }

  private JsonObject buildContentInfoEsIndexSrc(String id, String contentFormat, String text) {
    if (StringUtils.isNotBlank(text)) {
      ContentInfoEio contentInfoEo = new ContentInfoEio();
      contentInfoEo.setId(id);
      contentInfoEo.setContentFormat(contentFormat != null ? contentFormat : IndexerConstants.TYPE_RESOURCE);
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

  @Override
  public void updateBrokenStatus(String ids, boolean markBroken) {
    try{
      int brokenStatus = 0;
      if(markBroken){
        brokenStatus = 1;
      }
      BulkRequestBuilder bulkRequest = getClient().prepareBulk();
      String[] idArr = ids.split(",");
      for(String resourceId : idArr){
        if(!resourceId.isEmpty()){
          Map<String, Object> paramsField = new HashMap<>();
          StringBuffer scriptQuery = new StringBuffer();
          Map<String, Object> fieldValues = new HashMap<>();
          
          fieldValues.put(ScoreConstants.BROKEN_STATUS, brokenStatus);
          if(markBroken){
            fieldValues.put(IndexFields.PUBLISH_STATUS, IndexerConstants.UNPUBLISH_STATUS);
          }
          IndexScriptBuilder.buildScript(resourceId, paramsField, scriptQuery, fieldValues);
          LOGGER.debug("script : " + scriptQuery.toString());
          LOGGER.debug("param fields : " + paramsField.toString());
          bulkRequest.add(getClient().prepareUpdate(IndexNameHolder.getIndexName(EsIndex.RESOURCE), IndexerConstants.TYPE_RESOURCE, resourceId).setScript(new Script(scriptQuery.toString(), ScriptType.INLINE, "groovy", paramsField)));
        }
      }
      
      BulkResponse bulkResponse = bulkRequest.execute().actionGet();
      BulkItemResponse[] responses =  bulkResponse.getItems();
      for(BulkItemResponse response : responses){
        if(response.isFailed()){
          INDEX_FAILURES_LOGGER.error(" bulkIndexBrokenStatus : Failed  id : " + response.getId() + " Exception "+response.getFailureMessage());
        }
        else if(markBroken){
          trackDeletes(response.getId(), IndexerConstants.TYPE_RESOURCE);
        }
      }

      if(bulkResponse.hasFailures()){
        throw new Exception(bulkResponse.buildFailureMessage());
      }
      else {
        LOGGER.debug("Successfully updated broken status bulk");
      }
    }
    catch(Exception e){
      LOGGER.error("Failed to update broken status fields ", e.getMessage());
    }
  }

}
