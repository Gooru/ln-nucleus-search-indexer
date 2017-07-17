package org.gooru.nucleus.search.indexers.app.services;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class RubricIndexServiceImpl implements RubricIndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(RubricIndexServiceImpl.class);
  
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
  
}
