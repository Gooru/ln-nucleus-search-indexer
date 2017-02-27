package org.gooru.nucleus.search.indexers.app.services;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class ResourceIndexServiceImpl implements ResourceIndexService {

  public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceIndexServiceImpl.class);
  
  @Override
  public void deleteIndexedResource(String key, String type) throws Exception {
    try {
      LOGGER.debug("RISI->deleteIndexedResource : Processing delete resource for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_DELETED_RESOURCE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.RESOURCE_NOT_DELETED);
      IndexService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception ex) {
      LOGGER.error("RISI->deleteIndexedResource : Delete resource from index failed for resource id : " + key + " Exception : " + ex);
      throw new Exception(ex);
    }
  }
  
  @Override
  public void deleteIndexedCopiedResource(String key, String type) throws Exception {
    try {
      LOGGER.debug("RISI->deleteIndexedCopiedResource : Processing delete copied resource for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_DELETED_QUESTION_OR_COPIED_RESOURCE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.COPIED_RESOURCE_NOT_DELETED);
      IndexService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception ex) {
      LOGGER.error("RISI->deleteIndexedCopiedResource : Delete resource from index failed for copied resource id : " + key + " Exception : " + ex);
      throw new Exception(ex);
    }
  }
  
  @Override
  public void deleteIndexedQuestion(String key, String type) throws Exception {
    try {
      LOGGER.debug("RISI->deleteIndexedQuestion : Processing delete question for id : " + key);
      ProcessorContext context = new ProcessorContext(key, ExecuteOperationConstants.GET_DELETED_QUESTION_OR_COPIED_RESOURCE);
      JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
      ValidationUtil.rejectIfNotDeleted(result, ErrorMsgConstants.QUESTION_NOT_DELETED);
      IndexService.instance().deleteDocuments(key, EsIndexServiceImpl.getIndexByType(type), EsIndexServiceImpl.getIndexTypeByType(type));
    } catch (Exception ex) {
      LOGGER.error("RISI->deleteIndexedQuestion : Delete question from index failed for question id : " + key + " Exception : " + ex);
      throw new Exception(ex);
    }
  }
}
