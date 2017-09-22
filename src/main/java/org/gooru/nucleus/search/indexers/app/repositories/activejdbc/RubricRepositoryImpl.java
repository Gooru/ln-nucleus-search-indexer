package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Rubric;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class RubricRepositoryImpl extends BaseIndexRepo implements RubricRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(RubricRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";
  
  @Override
  public JsonObject getRubric(String contentID) {
    LOGGER.debug("RubricRepositoryImpl:getRubric: " + contentID);

    Rubric result = Rubric.findById(getPGObject("id", UUID_TYPE, contentID));

    JsonObject returnValue = null;
    if (result != null && !result.getBoolean(Rubric.IS_DELETED) && result.getBoolean(Rubric.IS_RUBRIC)) {
      returnValue = new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
  }

  @Override
  public JsonObject getDeletedRubric(String rubricId) {
    JsonObject returnValue = null;
    List<Rubric> rubrics = Rubric.where(Rubric.FETCH_DELETED_QUERY, rubricId, true);
    if (rubrics.size() < 1) {
      LOGGER.warn("Rubric id: {} not present in DB", rubricId);
    }
    if(rubrics.size() > 0){
      Rubric rubric = rubrics.get(0);
      if (rubric != null) {
        returnValue = new JsonObject(rubric.toJson(false));
      }
    }
    return returnValue;
  }
  
  @Override
  public Integer getQuestionCountByRubricId(String rubricId) {
    Integer questionCount = 0;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      List countList = db.firstColumn(Rubric.FETCH_MAPPED_QUESTIONS, rubricId);
      if (countList == null || countList.size() < 1) {
        LOGGER.warn("No Mapped Questions for Rubric : {}", rubricId);
        return questionCount;
      }
      questionCount = ((Long) countList.get(0)).intValue();
    } catch (Exception e) {
      LOGGER.error("Not able to fetch Mapped question count for rubric : {} error : {}", rubricId, e);
    } finally {
      closeDBConn(db);
    }
    return questionCount;
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
