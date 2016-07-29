package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;

import javax.sql.DataSource;

import org.gooru.nucleus.search.indexers.app.processors.responses.ExecutionResult;


public interface DBHandler {
  ExecutionResult<JsonObject> checkSanity();

  ExecutionResult<JsonObject> validateRequest();

  ExecutionResult<JsonObject> executeRequest();
  
  DataSource getDataSource();

  boolean handlerReadOnly();

  String getDatabase();

}
