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
    if (context.getContentId() == null || context.getContentId().isEmpty()) {
      LOGGER.debug("checkSanity() failed");
      return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.FAILED);
    }
    LOGGER.debug("checkSanity() passed");
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<JsonObject> validateRequest() {
    return new ExecutionResult<JsonObject>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<JsonObject> executeRequest() {
    JsonObject result = null;
    String operationName = context.getOperationName();
    LOGGER.debug("Repository operation name : " + operationName);
    try {
      switch (operationName) {
        case ExecuteOperationConstants.GET_RESOURCE:
          result = ContentRepository.instance().getResource(context.getContentId());
          break;

        case ExecuteOperationConstants.GET_COLLECTION_QUESTION_PARENT_CONTENT_IDS:
          result = ContentRepository.instance().getQuestionAndParentContentIds(context.getContentId());
          break;

        case ExecuteOperationConstants.GET_COLLECTION:
          result = CollectionRepository.instance().getCollection(context.getContentId());
          break;

        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          throw new InvalidRequestException();
      }
      if (result != null) {
        LOGGER.debug("Processed operation : " + operationName + " data : " + result.toString());
        return new ExecutionResult<JsonObject>(result, ExecutionStatus.SUCCESSFUL);
      }
    } catch (Exception ex) {
      LOGGER.error("Failed to fetch operation " + operationName + " content Id : " + context.getContentId() + " Exception : " + ex);
    }
    return new ExecutionResult<JsonObject>(null, ExecutionStatus.FAILED);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }


}
