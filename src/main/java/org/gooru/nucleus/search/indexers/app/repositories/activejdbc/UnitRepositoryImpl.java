package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Course;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Unit;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class UnitRepositoryImpl extends BaseIndexRepo implements UnitRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getUnit(String unitId) {
    Unit result = Unit.findById(getPGObject("id", UUID_TYPE, unitId));

    JsonObject returnValue = null;
    if (result != null && !result.getBoolean(Course.IS_DELETED)) {
      returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
  }

  @Override
  public JsonObject getDeletedUnit(String unitId) {
    JsonObject returnValue = null;
    try {
      List<Unit> units = Unit.where(Unit.FETCH_DELETED_QUERY, unitId, true);
      if (units.size() < 1) {
        LOGGER.warn("Unit id: {} not present in DB", unitId);
      }
      if (units.size() > 0) {
        Unit unit = units.get(0);
        if (unit != null) {
          returnValue = new JsonObject(unit.toJson(false));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to fetch deleted unit from DB : {} error : {}", unitId, e);
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
  public JsonObject getUnitById(String unitId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      Unit result = Unit.findById(getPGObject("id", UUID_TYPE, unitId));
      if (result != null && !result.getBoolean(Unit.IS_DELETED)) {
        returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch unit : {} error : {}", unitId, e);
    }
    closeDefaultDBConn(db);
    return returnValue;
  }
  
  @Override
  public LazyList<Unit> getUnitByCourseId(String courseId) {
    LazyList<Unit> lessons = null;
    DB db = getDefaultDataSourceDBConnection();
    try{
      openDefaultDBConnection(db);
      lessons = Unit.where(Unit.GET_UNIT_BY_COURSE_ID, courseId, false);
      if (lessons.size() < 1) {
        LOGGER.warn("Units for course: {} not present in DB", courseId);
      }
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch units for course : {} error : {}", courseId, e);
    }
    closeDefaultDBConn(db);
    return lessons;
  }
  
}
