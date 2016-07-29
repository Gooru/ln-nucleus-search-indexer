package org.gooru.nucleus.search.indexers.app.processors;

import io.vertx.core.json.JsonObject;

public class ProcessorContext {

  private final String id;
  private final String contentFormat;
  private final JsonObject filters;
  private final String operationName;
  private final JsonObject request;

  public ProcessorContext(String contentId, String contentFormat, String operationName, JsonObject filters, JsonObject request) {
    if (contentId == null || operationName == null) {
      throw new IllegalStateException("Processor Context creation failed because of invalid values");
    }
    this.id = contentId;
    this.contentFormat = contentFormat;
    this.filters = (filters != null ? filters.copy() : null);
    this.request = request != null ? request.copy() : null;
    this.operationName = operationName;
  }

  public ProcessorContext(String contentId, String operationName) {
    if (contentId == null || operationName == null) {
      throw new IllegalStateException("Processor Context creation failed because of invalid values");
    }
    this.id = contentId;
    this.contentFormat = null;
    this.filters = null;
    this.request = null;
    this.operationName = operationName;
  }

  public String getId() {
    return id;
  }

  public String getContentFormat() {
    return contentFormat;
  }

  public JsonObject getFilters() {
    return filters;
  }

  public JsonObject getRequest() {
      return this.request;
  }

  public String getOperationName() {
    return operationName;
  }

}
