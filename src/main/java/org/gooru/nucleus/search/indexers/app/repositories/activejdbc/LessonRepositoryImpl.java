package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Course;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Lesson;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class LessonRepositoryImpl extends BaseIndexRepo implements LessonRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getLesson(String lessonId) {
    Lesson result = Lesson.findById(getPGObject("id", UUID_TYPE, lessonId));
    //LOGGER.debug("UnitRepositoryImpl : getUnit : " + result);

    JsonObject returnValue = null;
    if (result != null && !result.getBoolean(Course.IS_DELETED)) {
      returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
  }

  @Override
  public JsonObject getDeletedLesson(String lessonId) {
    JsonObject returnValue = null;
    List<Lesson> lessons = Lesson.where(Lesson.FETCH_DELETED_QUERY, lessonId, true);
    if (lessons.size() < 1) {
      LOGGER.warn("Lesson id: {} not present in DB", lessonId);
    }
    if(lessons.size() > 0){
      Lesson lesson = lessons.get(0);
      if (lesson != null) {
        returnValue = new JsonObject(lesson.toJson(false));
      }
    }
    return returnValue;
  }

  @Override
  public Integer getLessonCountByUnitId(String unitId) {
    Integer lessonCount = 0;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openConnection(db);
      Long lessonCountL = Lesson.count(Lesson.GET_LESSON_BY_UNIT_ID, unitId, false);
      LOGGER.debug("Lesson count : {} for course : {}", lessonCountL, unitId);
      lessonCount =  lessonCountL != null ? lessonCountL.intValue() : 0;
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lesson count for unit : {} error : {}", unitId, e);
    }
    closeDBConn(db);
    return lessonCount;
  }
  
  @Override
  public JsonObject getLessonById(String lessonId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      Lesson result = Lesson.findById(getPGObject("id", UUID_TYPE, lessonId));
      if (result != null && !result.getBoolean(Lesson.IS_DELETED)) {
        returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch lesson : {} error : {}", lessonId, e);
    }
    closeDBConn(db);
    return returnValue;
  }
  
  @Override
  public Integer getLessonCountByCourseId(String courseId) {
    Integer lessonCount = 0;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openConnection(db);
      Long lessonCountL = Lesson.count(Lesson.GET_LESSON_BY_COURSE_ID, courseId, false);
      LOGGER.debug("Lesson count : {} for course : {}", lessonCountL, courseId);
      lessonCount =  lessonCountL != null ? lessonCountL.intValue() : 0;
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lesson count for course : {} error : {}", courseId, e);
    }
    closeDBConn(db);
    return lessonCount;
  }
  
  @Override
  public LazyList<Lesson> getLessonByUnitId(String unitId) {
    LazyList<Lesson> lessons = null;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openConnection(db);
      lessons = Lesson.where(Lesson.GET_LESSON_BY_UNIT_ID, unitId, false);
      if (lessons.size() < 1) {
        LOGGER.warn("Lessons for unit: {} not present in DB", unitId);
      }
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lessons for unit : {} error : {}", unitId, e);
    }
    closeDBConn(db);
    return lessons;
  }
  
  @Override
  public LazyList<Lesson> getLessonByCourseId(String courseId) {
    LazyList<Lesson> lessons = null;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openConnection(db);
      lessons = Lesson.where(Lesson.GET_LESSON_BY_COURSE_ID, courseId, false);
      if (lessons.size() < 1) {
        LOGGER.warn("Lessons for course: {} not present in DB", courseId);
      }
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lessons for course : {} error : {}", courseId, e);
    }
    closeDBConn(db);
    return lessons;
  }
  
  private PGobject getPGObject(String field, String type, String value) {
    PGobject pgObject = new PGobject();
    pgObject.setType(type);
    try {
      pgObject.setValue(value);
      return pgObject;
    } catch (SQLException e) {
      LOGGER.error("Not able to set value for field: {}, type: {}, value: {}", field, type, value);
      return null;
    }
  }
}
