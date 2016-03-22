package org.gooru.nucleus.search.indexers.app.processors;

import io.vertx.core.json.JsonObject;

public final class ProcessorBuilder {

  public static Processor build(String operationName, JsonObject message) {
    return new MessageProcessor(message, operationName);
  }

  private ProcessorBuilder(JsonObject message) {
    throw new AssertionError();
  }
}
