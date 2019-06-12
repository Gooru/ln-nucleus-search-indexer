package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Lesson;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonObject;

public interface LessonRepository {

  static LessonRepository instance() {
    return new LessonRepositoryImpl();
  }
  JsonObject getLesson(String lessonId);
    
  JsonObject getDeletedLesson(String lessonId);
  
  Integer getLessonCountByUnitId(String unitId);
  
  LazyList<Lesson> getLessonByUnitId(String unitId);
  
  LazyList<Lesson> getLessonByCourseId(String courseId);
  
  Integer getLessonCountByCourseId(String courseId);
  
  JsonObject getLessonById(String lessonId);
  
  JsonObject getDeletedLessonsOfCourse(String courseId);
  
  JsonObject getDeletedLessonsOfUnit(String unitId);
  
  JsonObject getLessonsOfCourse(String courseId);
  
  JsonObject getLessonsOfUnit(String unitId);

}
