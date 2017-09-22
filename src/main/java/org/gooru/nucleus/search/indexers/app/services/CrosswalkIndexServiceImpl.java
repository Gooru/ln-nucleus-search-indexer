package org.gooru.nucleus.search.indexers.app.services;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class CrosswalkIndexServiceImpl implements CrosswalkIndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(CrosswalkIndexServiceImpl.class);
  
  @Override
  public void deleteIndexedCrosswalk(String key, String type) throws Exception {
    try {
      LOGGER.debug("CwISI->deleteIndexedCrosswalk : Processing delete crosswalk for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_CROSSWALK);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.CROSSWALK_UNAVAILABLE);
      IndexService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception ex) {
      LOGGER.error("CwISI->deleteIndexedCrosswalk : Delete from index failed for crosswalk id : " + key + " Exception : " + ex);
      throw new Exception(ex);
    }
  }
}
