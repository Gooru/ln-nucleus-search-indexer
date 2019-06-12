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
  
  public static IndexEventHandler buildStatisticsHandler(JsonObject eventJson){
    return new StatisticsEventsHandler(eventJson);
  }
  
  public static IndexEventHandler buildCourseHandler(JsonObject eventJson){
    return new CourseEventsHandler(eventJson);
  }
  
  public static IndexEventHandler buildUnitHandler(JsonObject eventJson){
    return new UnitEventsHandler(eventJson);
  }
  
  public static IndexEventHandler buildLessonHandler(JsonObject eventJson){
    return new LessonEventsHandler(eventJson);
  }
  
  public static IndexEventHandler buildKeywordsHandler(JsonObject eventJson){
    return new KeywordEventsHandler(eventJson);
  }
  
  public static IndexEventHandler buildRubricHandler(JsonObject eventJson){
    return new RubricEventsHandler(eventJson);
  }

}
