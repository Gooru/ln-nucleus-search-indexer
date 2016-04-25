package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult;
import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CollectionRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchContentHandler implements DBHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchContentHandler.class);
  private final ProcessorContext context;

  public FetchContentHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<JsonObject> checkSanity() {
    if (context.getId() == null || context.getId().isEmpty()) {
      LOGGER.debug("checkSanity() failed");
      return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.FAILED);
    }
    LOGGER.debug("checkSanity() passed");
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<JsonObject> validateRequest() {
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<JsonObject> executeRequest() {
    JsonObject result = null;
    String operationName = context.getOperationName();
    LOGGER.debug("Repository operation name : " + operationName);
    try {
      switch (operationName) {
        case ExecuteOperationConstants.GET_RESOURCE:
          result = ContentRepository.instance().getResource(context.getId());
          break;

        case ExecuteOperationConstants.GET_COLLECTION_QUESTION_PARENT_CONTENT_IDS:
          result = ContentRepository.instance().getQuestionAndParentContentIds(context.getId());
          break;

        case ExecuteOperationConstants.GET_COLLECTION:
          result = CollectionRepository.instance().getCollection(context.getId());
          break;

        case ExecuteOperationConstants.GET_DELETED_RESOURCE:
          result = ContentRepository.instance().getDeletedContent(context.getId());
          break;

        case ExecuteOperationConstants.GET_DELETED_COLLECTION:
          result = CollectionRepository.instance().getDeletedCollection(context.getId());
          break;

        case ExecuteOperationConstants.GET_USER_RESOURCES:
          result = ContentRepository.instance().getUserResources(context.getId());
          break;
          
        case ExecuteOperationConstants.GET_USER_COLLECTIONS:
          result = CollectionRepository.instance().getUserCollections(context.getId());
          break;

        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          throw new InvalidRequestException();
      }
      if (result != null) {
        LOGGER.debug("Processed operation : " + operationName + " data : " + result.toString());
        return new ExecutionResult<>(result, ExecutionStatus.SUCCESSFUL);
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch operation " + operationName + " content Id : " + context.getId() + " Exception : " + ex);
    }
    return new ExecutionResult<>(null, ExecutionStatus.FAILED);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }


}
