package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Course;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Unit;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.jsoup.nodes.Entities;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class CourseRepositoryImpl extends BaseIndexRepo implements CourseRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getCourse(String courseId) {
    Course result = Course.findById(getPGObject("id", UUID_TYPE, courseId));
    //LOGGER.debug("CourseRepositoryImpl : getCourse : " + result);

    JsonObject returnValue = null;
    if (result != null && !result.getBoolean(Course.IS_DELETED)) {
      returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
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

  @Override
  public Integer getUnitCount(String courseId) {
    Integer unitCount = 0;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openConnection(db);
      Long unitCountL = Unit.count(Unit.GET_UNIT_COUNT, courseId, false);
      LOGGER.debug("Unit count : {} for course : {}", unitCountL, courseId);
      unitCount =  unitCountL != null ? unitCountL.intValue() : 0;
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch unit count for course : {} error : {}", courseId, e);
    }
    closeDBConn(db);
    return unitCount;
  }

  @Override
  public JsonObject getDeletedCourse(String courseId) {
    JsonObject returnValue = null;
    List<Course> courses = Course.where(Course.FETCH_DELETED_QUERY, courseId, true);
    if (courses.size() < 1) {
      LOGGER.warn("Course id: {} not present in DB", courseId);
    }
    if(courses.size() > 0){
      Course course = courses.get(0);
      if (course != null) {
        returnValue = new JsonObject(course.toJson(false));
      }
    }
    return returnValue;
  }
  
  @Override
  public JsonObject getCourseById(String courseId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      Course result = Course.findById(getPGObject("id", UUID_TYPE, courseId));
      // LOGGER.debug("CourseRepositoryImpl : getCourseById : " + result);

      if (result != null && !result.getBoolean(Course.IS_DELETED)) {
        returnValue = new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch unit count for course : {} error : {}", courseId, e);
    }
    closeDBConn(db);
    return returnValue;
  }
  
  @Override
  public Boolean isFeatured(String courseId) {
    Boolean isFeatured = false;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      Course result = Course.findById(getPGObject("id", UUID_TYPE, courseId));
      // LOGGER.debug("CourseRepositoryImpl : isFeatured : " + result);

      if (result != null && !result.getBoolean(Course.IS_DELETED)) {
        if (result.getString(EntityAttributeConstants.PUBLISH_STATUS).equalsIgnoreCase(IndexerConstants.PUBLISHED)) {
          isFeatured = true;
        }
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch unit count for course : {} error : {}", courseId, e);
    }
    closeDBConn(db);
    return isFeatured;
  }

}
