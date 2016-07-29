package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import javax.sql.DataSource;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult;
import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexTrackerRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.CollectionIndexDelete;
import org.gooru.nucleus.search.indexers.app.repositories.entities.ResourceIndexDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class IndexTrackerHandler implements DBHandler {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(IndexTrackerHandler.class);
  private final ProcessorContext context;

  public IndexTrackerHandler(ProcessorContext context) {
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
    String operationName = context.getOperationName();
    LOGGER.debug("Repository operation name : " + operationName);
    try {
      switch (operationName) {
        case ExecuteOperationConstants.SAVE_DELETED_RESOURCE:
          IndexTrackerRepository.instance().saveDeletedResource(context.getId(), context.getRequest(), ResourceIndexDelete.INSERT_RESOURCE_ALLOWED_FIELDS);;
          break;

        case ExecuteOperationConstants.SAVE_DELETED_COLLECTION:
          IndexTrackerRepository.instance().saveDeletedCollection(context.getId(), context.getRequest(), CollectionIndexDelete.INSERT_COLLECTION_ALLOWED_FIELDS);;
          break;

        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          throw new InvalidRequestException();
      }
      
    } catch (Exception ex) {
      LOGGER.error("Failed to execute operation " + operationName + " Id : " + context.getId() + " Exception : " + ex);
    }
    return new ExecutionResult<>(null, ExecutionStatus.FAILED);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

  @Override
  public DataSource getDataSource() {
    return DataSourceRegistry.getInstance().getIndexTrackerDataSource();
  }

  @Override
  public String getDatabase() {
    return DataSourceRegistry.getInstance().getIndexTrackerDatabase();
  }
}
