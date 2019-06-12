package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

public final class IndexHandlerBuilder {

  private IndexHandlerBuilder() {
    throw new AssertionError();
  }

  public static IndexHandler buildResourceIndexHandler() {
    return new ResourceIndexHandler();
  }
  
  public static IndexHandler buildQuestionIndexHandler() {
    return new QuestionAndResourceReferenceIndexHandler();
  }

  public static IndexHandler buildCollectionIndexHandler() {
    return new CollectionIndexHandler();
  }
  
  public static IndexHandler buildCourseIndexHandler(){
    return new CourseIndexHandler();
  }
  
  public static IndexHandler buildRubricIndexHandler(){
    return new RubricIndexHandler();
  }
  
  public static IndexHandler buildUnitIndexHandler(){
    return new UnitIndexHandler();
  }
  
  public static IndexHandler buildLessonIndexHandler(){
    return new LessonIndexHandler();
  }
  
}
