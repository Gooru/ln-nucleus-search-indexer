package org.gooru.nucleus.search.indexers.app.services;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.IdIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 */
public class EsDeleteServiceImpl extends BaseIndexService implements DeleteService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(EsDeleteServiceImpl.class);

  @Override
  public void deleteDocuments(String indexableIds, String indexName, String type) throws IOException {
    for (String key : indexableIds.split(",")) {
      deleteFromIndex(indexName, getIndexTypeByType(type), key);

      // Delete from CI index
      deleteFromIndex(indexName, getIndexTypeByType(type), key);

      trackDeletes(key, getIndexTypeByType(type));
    }
  }

  @Override
  public void trackDeletes(String key, String type) {
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
        case IndexerConstants.TYPE_GUT:
          GutIndexService.instance().deleteIndexedGut(deletableId, type);
          break;
        case IndexerConstants.TYPE_RESOURCE_REFERENCE:
          ResourceIndexService.instance().deleteIndexedResourceReference(deletableId, type);
          break;
        default:
          LOGGER.error("Invalid type passed in, not able to delete");
          throw new InvalidRequestException("Invalid type : " + type);
        }
      }
    };
  }
  
  @Override
  public void bulkDeleteDocuments(JsonArray jsonArr, String contentType, String index) {

    try{
      
      /*if(!IndexerConstants.RESOURCE_FORMATS.matcher(contentType).matches() || !contentType.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)){
        throw new Exception("Invalid type in bulkIndexDocuments() !");
      }*/
      
      int batchSize = 50;
      int processedBatchSize = 0;
      LOGGER.debug("bulkDeleteDocuments()-> content type : "+contentType +" total size : " + jsonArr.size());
      while(true) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Object jsonObjStr : jsonArr) {
          String jsonStr = (String)(jsonObjStr);
          JsonObject data = null;
          String id = null;
          try {
            data = new JsonObject(jsonStr);
          } catch (Exception e) {
            id = jsonStr;
          }
          if (data != null && !data.isEmpty()) {
            id = data.getString(EntityAttributeConstants.ID);
            if (contentType.equalsIgnoreCase("unit")) id = data.getString(EntityAttributeConstants.UNIT_ID);
            if (contentType.equalsIgnoreCase("lesson")) id = data.getString(EntityAttributeConstants.LESSON_ID);
          } else if (jsonStr != null && !jsonStr.isEmpty()){
            id = jsonStr;
          }
          if (id != null) {
            DeleteRequest delete = new DeleteRequest(index, getIndexTypeByType(contentType), id); 
            bulkRequest.add(delete);
          }
          processedBatchSize++;
          if (processedBatchSize > 0 && (batchSize == bulkRequest.numberOfActions() || processedBatchSize == jsonArr.size())) {
            BulkResponse bulkResponse = getHighLevelClient().bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
              BulkItemResponse[] responses = bulkResponse.getItems();
              for (BulkItemResponse response : responses) {
                if (response.isFailed()) {
                  INDEX_FAILURES_LOGGER.error("bulkDeleteDocuments() : Failed  id : " + response.getId());
                }
              }
              throw new Exception(bulkResponse.buildFailureMessage());
            } else {
              LOGGER.debug("bulkDeleteDocuments() -> Successfull, totalProcessedTillCurrentBatch : {} !", processedBatchSize);
            }
            if (processedBatchSize < jsonArr.size()) {
              continue;
            }
            break;
          }
        }
        if (processedBatchSize == jsonArr.size()) {
          break;
        }
      }
      //LOGGER.debug("bulkIndexDocuments() -> Successfully Completed for content type : {} total documents indexed :{}", contentType, processedBatchSize);
    }
    catch(Exception e){
      LOGGER.error("EIS->bulkDeleteDocuments : Failed", e);
    }
  }

}
