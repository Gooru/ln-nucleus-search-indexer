package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import io.vertx.core.json.JsonObject;

public final class EventHandlerBuilder {

  private EventHandlerBuilder() {
    throw new AssertionError();
  }

  public static IndexEventHandler buildCollectionHandler(JsonObject eventJson) {
    return new CollectionEventsHandler(eventJson);
  }

  public static IndexEventHandler buildResourceHandler(JsonObject eventJson) {
    return new ResourceEventsHandler(eventJson);
  }

  public static IndexEventHandler buildUserHandler(JsonObject eventJson) {
    return new UserEventsHandler(eventJson);
  }

}
