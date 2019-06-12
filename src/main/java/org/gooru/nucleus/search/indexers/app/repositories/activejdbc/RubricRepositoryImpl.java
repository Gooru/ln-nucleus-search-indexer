package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Rubric;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.common.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
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
      openDefaultDBConnection(db);
      List countList = db.firstColumn(Rubric.FETCH_MAPPED_QUESTIONS, rubricId);
      if (countList == null || countList.size() < 1) {
        LOGGER.warn("No Mapped Questions for Rubric : {}", rubricId);
        return questionCount;
      }
      questionCount = ((Long) countList.get(0)).intValue();
    } catch (Exception e) {
      LOGGER.error("Not able to fetch Mapped question count for rubric : {} error : {}", rubricId, e);
    } finally {
      closeDefaultDBConn(db);
    }
    return questionCount;
  }
  
  @Override
  public JsonObject getDeletedRubricsOfCourse(String courseId) {
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_COURSE, courseId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Rubrics for course : {} not present in DB", courseId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getDeletedRubricsOfUnit(String unitId) {
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_UNIT, unitId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Rubrics for unit : {} not present in DB", unitId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getDeletedRubricsOfLesson(String lessonId) {
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_LESSON, lessonId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Rubrics for lesson : {} not present in DB", lessonId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getDeletedRubricsOfItem(String collectionId) {
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_COLLECTION, collectionId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Rubrics for container : {} not present in DB", collectionId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }

  @Override
  public JsonObject getRubricsOfCourse(String courseId) {
    JsonArray responseArray = new JsonArray();
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_COURSE, courseId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("Rubrics for course : {} not present in DB", courseId);
      }
      for(Rubric content : contents){
        responseArray.add(content.toJson(false));
      }
    }
    return new JsonObject().put("rubrics", responseArray);
  }
  
  @Override
  public JsonObject getRubricsOfUnit(String unitId) {
    JsonArray responseArray = new JsonArray();
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_UNIT, unitId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("Rubrics for unit : {} not present in DB", unitId);
      }
      for(Rubric content : contents){
        responseArray.add(content.toJson(false));
      }
    }
    return new JsonObject().put("rubrics", responseArray);
  }
  
  @Override
  public JsonObject getRubricsOfLesson(String lessonId) {
    JsonArray responseArray = new JsonArray();
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_LESSON, lessonId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("Rubrics for lesson : {} not present in DB", lessonId);
      }
      for(Rubric content : contents){
        responseArray.add(content.toJson(false));
      }
    }
    return new JsonObject().put("rubrics", responseArray);
  }
  
  @Override
  public JsonObject getRubricsOfItem(String collectionId) {
    JsonArray responseArray = new JsonArray();
    LazyList<Rubric> contents = Rubric.where(Rubric.GET_RUBRICS_OF_COLLECTION, collectionId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("Rubrics for container : {} not present in DB", collectionId);
      }
      for(Rubric content : contents){
        responseArray.add(content.toJson(false));
      }
    }
    return new JsonObject().put("rubrics", responseArray);
  }

  private void populateResponseWithRelatedIds(LazyList<Rubric> contents, JsonObject result) {
    JsonArray rubricIds = new JsonArray();
    if (contents.size() > 0) {
      for (Rubric content : contents) {
        rubricIds.add(Convert.toString(content.get(EntityAttributeConstants.ID)));
      }
    }
    result.put(IndexerConstants.RUBRIC_IDS, rubricIds);
  }
  
}
