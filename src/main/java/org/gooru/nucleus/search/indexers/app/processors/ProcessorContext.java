package org.gooru.nucleus.search.indexers.app.processors;

import io.vertx.core.json.JsonObject;

public class ProcessorContext {

  private final String contentId;
  private final String contentFormat;
  private final JsonObject filters;
  private final String operationName;

  public ProcessorContext(String contentId, String contentFormat, String operationName, JsonObject filters) {
    if (contentId == null || operationName == null) {
      throw new IllegalStateException("Processor Context creation failed because of invalid values");
    }
    this.contentId = contentId;
    this.contentFormat = contentFormat;
    this.filters = (filters != null ? filters.copy() : null);
    this.operationName = operationName;
  }

  public ProcessorContext(String contentId, String operationName) {
    if (contentId == null || operationName == null) {
      throw new IllegalStateException("Processor Context creation failed because of invalid values");
    }
    this.contentId = contentId;
    this.contentFormat = null;
    this.filters = null;
    this.operationName = operationName;
  }

  public String getContentId() {
    return contentId;
  }

  public String getContentFormat() {
    return contentFormat;
  }

  public JsonObject getFilters() {
    return filters;
  }

  public String getOperationName() {
    return operationName;
  }

}
