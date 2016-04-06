package org.gooru.nucleus.search.indexers.app.processors;

import io.vertx.core.json.JsonObject;

public final class ProcessorBuilder {

  private ProcessorBuilder(JsonObject message) {
    throw new AssertionError();
  }

  public static Processor build(JsonObject message) {
    return new MessageProcessor(message);
  }
}
