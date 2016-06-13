package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Course;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Unit;
import org.javalite.activejdbc.Base;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class CourseRepositoryImpl implements CourseRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getCourse(String courseId) {
    Course result = Course.findById(getPGObject("id", UUID_TYPE, courseId));
    LOGGER.debug("CourseRepositoryImpl : getCourse : " + result);

    JsonObject returnValue = null;
    if (result != null && !result.getBoolean(Course.IS_DELETED)) {
      returnValue = new JsonObject(result.toJson(false));
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
    try{
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      Long unitCountL = Unit.count(Unit.GET_UNIT_COUNT, courseId, false);
      LOGGER.debug("Unit count : {} for course : {}", unitCountL, courseId);
      unitCount =  unitCountL != null ? unitCountL.intValue() : 0;
    }
    catch(Exception e){
      LOGGER.error("Not able to fetch unit count for course : {} error : {}", courseId, e);
    }
    Base.close();
    return unitCount;
  }

}
