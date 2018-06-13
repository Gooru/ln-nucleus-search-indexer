package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Course;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Unit;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
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

  @Override
  public Integer getUnitCount(String courseId) {
    Integer unitCount = 0;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openDefaultDBConnection(db);
      Long unitCountL = Unit.count(Unit.GET_UNIT_COUNT, courseId, false);
      LOGGER.debug("Unit count : {} for course : {}", unitCountL, courseId);
      unitCount =  unitCountL != null ? unitCountL.intValue() : 0;
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch unit count for course : {} error : {}", courseId, e);
    }
    closeDefaultDBConn(db);
    return unitCount;
  }

  @Override
  public JsonObject getDeletedCourse(String courseId) {
    JsonObject returnValue = null;
    try {
      List<Course> courses = Course.where(Course.FETCH_DELETED_QUERY, courseId, true);
      if (courses.size() < 1) {
        LOGGER.warn("Course id: {} not present in DB", courseId);
      }
      if (courses.size() > 0) {
        Course course = courses.get(0);
        if (course != null) {
          returnValue = new JsonObject(course.toJson(false));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to fetch deleted course from DB : {} error : {}", courseId, e);
    }
    return returnValue;
  }
  
  @Override
  public JsonObject getCourseById(String courseId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      Course result = Course.findById(getPGObject("id", UUID_TYPE, courseId));
      // LOGGER.debug("CourseRepositoryImpl : getCourseById : " + result);

      if (result != null && !result.getBoolean(Course.IS_DELETED)) {
        returnValue = new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch course : {} error : {}", courseId, e);
    }
    closeDefaultDBConn(db);
    return returnValue;
  }
  
  @Override
  public Boolean isFeatured(String courseId) {
    Boolean isFeatured = false;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      Course result = Course.findById(getPGObject("id", UUID_TYPE, courseId));
      // LOGGER.debug("CourseRepositoryImpl : isFeatured : " + result);

      if (result != null && !result.getBoolean(Course.IS_DELETED)) {
        if (result.getString(EntityAttributeConstants.PUBLISH_STATUS).equalsIgnoreCase(IndexerConstants.PUBLISHED)) {
          isFeatured = true;
        }
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch featured flag for course : {} error : {}", courseId, e);
    }
    closeDefaultDBConn(db);
    return isFeatured;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public Long getUsedByStudentCount(String courseId) {
    Long count = 0L;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      List countList = db.firstColumn(Course.GET_USED_BY_STUDENT_COUNT, courseId);
      if (countList == null || countList.size() < 1) {
        LOGGER.warn("Students for course : {} not present in DB", courseId);
        return count;
      }
      count = ((Long) countList.get(0));
    } catch (Exception e) {
      LOGGER.error("Not able to fetch Students count for course : {} error : {}", courseId, e);
    } finally {
      closeDefaultDBConn(db);
    }
    return count;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Long getRemixedInClassCount(String courseId) {
    Long count = 0L;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      List countList = db.firstColumn(Course.GET_REMIXED_IN_CLASS_COUNT, courseId);
      if (countList == null || countList.size() < 1) {
        LOGGER.warn("RemixedInClass count for course : {} not present in DB", courseId);
        return count;
      }
      count = ((Long) countList.get(0));
    } catch (Exception e) {
      LOGGER.error("Not able to fetch RemixedInClass count for course : {} error : {}", courseId, e);
    } finally {
      closeDefaultDBConn(db);
    }
    return count;
  }


}
