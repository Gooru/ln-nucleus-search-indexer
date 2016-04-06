package org.gooru.nucleus.search.indexers.app.services;

import io.vertx.core.json.JsonObject;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.gooru.nucleus.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchRegistry;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.IdIterator;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.IndexScriptBuilder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SearchTeam
 */
public class EsIndexServiceImpl implements IndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(EsIndexServiceImpl.class);

  private static String getIndexByType(String type) {
    String indexName = null;
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION)) {
      indexName = IndexNameHolder.getIndexName(EsIndex.COLLECTION);
    }
    if (type.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
      indexName = IndexNameHolder.getIndexName(EsIndex.RESOURCE);
    }
    return indexName;
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
      public void execute(String indexableId) {
        if ((typeName.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE) || typeName.equalsIgnoreCase(IndexerConstants.TYPE_COLLECTION))) {
          if (!body.isEmpty()) {
            try {
              Map<String, Object> existingDocument = getDocument(indexableId, indexName, typeName);
              Map<String, Object> statisticsMap = (Map<String, Object>) existingDocument.get(ScoreConstants.STATISTICS_FIELD);
              setExistingStatisticsData(body, statisticsMap, typeName);
              getClient().prepareIndex(indexName, typeName, indexableId).setSource(EsIndexSrcBuilder.get(typeName).buildSource(body)).execute()
                         .actionGet();
            } catch (Exception e) {
              LOGGER.info("Exception while indexing");
              e.printStackTrace();
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
      getClient().prepareUpdate(IndexNameHolder.getIndexName(EsIndex.STATISTICS), IndexerConstants.TYPE_STATISTICS, id)
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

      @Override
      public void execute(String indexableId) throws Exception {
        try {
          // Fetch data from DB for given content Id
          ProcessorContext context = new ProcessorContext(indexableId, getExectueOperation(typeName));
          JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
          ValidationUtil.rejectIfNull(result, "DB return null data for id " + indexableId);

          // Get statistics data from backup index
          Map<String, Object> statisticsMap =
            getDocument(indexableId, IndexNameHolder.getIndexName(EsIndex.STATISTICS), IndexerConstants.TYPE_STATISTICS);
          LOGGER.debug("statistics index data : " + statisticsMap);
          result.put("isBuildIndex", true);
          setExistingStatisticsData(result, statisticsMap, typeName);
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
}
