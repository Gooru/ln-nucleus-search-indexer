package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult;

import io.vertx.core.json.JsonObject;


public interface DBHandler {
  ExecutionResult<JsonObject> checkSanity();

  ExecutionResult<JsonObject> validateRequest();

  ExecutionResult<JsonObject> executeRequest();

  boolean handlerReadOnly();
  
}
