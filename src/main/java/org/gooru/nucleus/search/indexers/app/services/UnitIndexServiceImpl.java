package org.gooru.nucleus.search.indexers.app.services;

import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class UnitIndexServiceImpl implements UnitIndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(UnitIndexServiceImpl.class);
 
  @Override
  public void setExistingStatisticsData(JsonObject result, Map<String, Object> contentInfoAsMap) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteIndexedUnit(String key, String type) throws Exception {
    try {
      LOGGER.debug("UISI->deleteIndexedUnit : Processing delete unit for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_DELETED_UNIT);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.UNIT_NOT_DELETED);
      IndexService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception ex) {
      LOGGER.error("UISI->deleteIndexedUnit : Delete unit from index failed for unit id : " + key + " Exception : " + ex);
      throw new Exception(ex);
    }
  }
}
