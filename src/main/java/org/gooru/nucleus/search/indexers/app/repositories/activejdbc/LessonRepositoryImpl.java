package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Course;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Lesson;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.common.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class LessonRepositoryImpl extends BaseIndexRepo implements LessonRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(LessonRepositoryImpl.class);
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
    try {
      List<Lesson> lessons = Lesson.where(Lesson.FETCH_DELETED_QUERY, lessonId, true);
      if (lessons.size() < 1) {
        LOGGER.warn("Lesson id: {} not present in DB", lessonId);
      }
      if (lessons.size() > 0) {
        Lesson lesson = lessons.get(0);
        if (lesson != null) {
          returnValue = new JsonObject(lesson.toJson(false));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to fetch deleted lesson from DB : {} error : {}", lessonId, e);
    }
    return returnValue;
  }

  @Override
  public Integer getLessonCountByUnitId(String unitId) {
    Integer lessonCount = 0;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openDefaultDBConnection(db);
      Long lessonCountL = Lesson.count(Lesson.GET_LESSON_BY_UNIT_ID, unitId, false);
      LOGGER.debug("Lesson count : {} for course : {}", lessonCountL, unitId);
      lessonCount =  lessonCountL != null ? lessonCountL.intValue() : 0;
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lesson count for unit : {} error : {}", unitId, e);
    }
    closeDefaultDBConn(db);
    return lessonCount;
  }
  
  @Override
  public JsonObject getLessonById(String lessonId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      Lesson result = Lesson.findById(getPGObject("id", UUID_TYPE, lessonId));
      if (result != null && !result.getBoolean(Lesson.IS_DELETED)) {
        returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch lesson : {} error : {}", lessonId, e);
    }
    closeDefaultDBConn(db);
    return returnValue;
  }
  
  @Override
  public Integer getLessonCountByCourseId(String courseId) {
    Integer lessonCount = 0;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openDefaultDBConnection(db);
      Long lessonCountL = Lesson.count(Lesson.GET_LESSONS_OF_COURSE, courseId, false);
      LOGGER.debug("Lesson count : {} for course : {}", lessonCountL, courseId);
      lessonCount =  lessonCountL != null ? lessonCountL.intValue() : 0;
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lesson count for course : {} error : {}", courseId, e);
    }
    closeDefaultDBConn(db);
    return lessonCount;
  }
  
  @Override
  public LazyList<Lesson> getLessonByUnitId(String unitId) {
    LazyList<Lesson> lessons = null;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openDefaultDBConnection(db);
      lessons = Lesson.where(Lesson.GET_LESSON_BY_UNIT_ID, unitId, false);
      if (lessons.size() < 1) {
        LOGGER.warn("Lessons for unit: {} not present in DB", unitId);
      }
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lessons for unit : {} error : {}", unitId, e);
    }
    closeDefaultDBConn(db);
    return lessons;
  }
  
  @Override
  public LazyList<Lesson> getLessonByCourseId(String courseId) {
    LazyList<Lesson> lessons = null;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openDefaultDBConnection(db);
      lessons = Lesson.where(Lesson.GET_LESSONS_OF_COURSE, courseId, false);
      if (lessons.size() < 1) {
        LOGGER.warn("Lessons for course: {} not present in DB", courseId);
      }
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch lessons for course : {} error : {}", courseId, e);
    }
    closeDefaultDBConn(db);
    return lessons;
  }
  
  @Override
  public JsonObject getDeletedLessonsOfCourse(String courseId) {
    LazyList<Lesson> contents = Lesson.where(Lesson.GET_LESSONS_OF_COURSE, courseId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Lessons for course : {} not present in DB", courseId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getDeletedLessonsOfUnit(String unitId) {
    LazyList<Lesson> contents = Lesson.where(Lesson.GET_LESSONS_OF_UNIT, unitId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Lessons for unit : {} not present in DB", unitId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getLessonsOfCourse(String courseId) {
    JsonArray responseArray = new JsonArray();
    LazyList<Lesson> contents = Lesson.where(Lesson.GET_LESSONS_OF_COURSE, courseId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("Lessons for course : {} not present in DB", courseId);
      }
      for(Lesson content : contents){
        responseArray.add(content.toJson(false));
      }
    }
    return new JsonObject().put("lessons", responseArray);
  }
  
  @Override
  public JsonObject getLessonsOfUnit(String unitId) {
    JsonArray responseArray = new JsonArray();
    LazyList<Lesson> contents = Lesson.where(Lesson.GET_LESSONS_OF_UNIT, unitId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("Lessons for unit : {} not present in DB", unitId);
      }
      for(Lesson content : contents){
        responseArray.add(content.toJson(false));
      }
    }
    return new JsonObject().put("lessons", responseArray);
  }

  private void populateResponseWithRelatedIds(LazyList<Lesson> contents, JsonObject result) {
    JsonArray lessonIds = new JsonArray();
    if (contents.size() > 0) {
      for (Lesson content : contents) {
        lessonIds.add(Convert.toString(content.get(EntityAttributeConstants.LESSON_ID)));
      }
    }
    result.put(IndexerConstants.LESSON_IDS, lessonIds);
  }

}
