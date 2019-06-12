package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.common.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CollectionRepositoryImpl extends BaseIndexRepo implements CollectionRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectionRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getCollection(String contentID) {
    LOGGER.debug("CollectionRepositoryImpl : getCollection : " + contentID);
    Collection result = null;
    LazyList<Collection>  list = Collection.where(Collection.GET_COLLECTION, contentID, false);
    
    if(list != null && list.size() > 0){
      result = list.get(0);
    }
   
    JsonObject returnValue = null;
    String courseId = null;

    if (result != null && !result.getBoolean(Collection.IS_DELETED)) {
      returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      courseId = returnValue.getString(EntityAttributeConstants.COURSE_ID);
    }
    
    // Set course title
    if (courseId != null) {
      JsonObject courseData = CourseRepository.instance().getCourse(courseId);
      if (courseData != null && !courseData.isEmpty()) {
        returnValue.put(IndexerConstants.COLLECTION_COURSE_ID, courseId);
        returnValue.put(IndexerConstants.COLLECTION_COURSE, courseData.getString(EntityAttributeConstants.TITLE));
      }
    }
    
   // LOGGER.debug("CollectionRepositoryImpl : getCollection : findById returned: " + returnValue);
    return returnValue;
  }

  @Override
  public JsonObject getAssessment(String contentID) {
    LOGGER.debug("CollectionRepositoryImpl : getAssessment : " + contentID);

    Collection result = Collection.findById(getPGObject("id", UUID_TYPE, contentID));

    JsonObject returnValue = null;
    String[] attributes =
      {"id", "title", "created_at", "updated_at", "creator_id", "original_creator_id", "original_collection_id", "publish_date", "format",
        "learning_objective", "collaborator", "orientation", "grading", "setting", "metadata", "taxonomy", "thumbnail", "visible_on_profile",
        "course_id", "unit_id", "lesson_id"};

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false, attributes));
    }
    LOGGER.debug("CollectionRepositoryImpl : getAssessment : afterAddingContainmentInfo : " + returnValue);

    return returnValue;
  }

  @Override
  public JsonObject getCollectionByType(String contentId, String format) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);
    LazyList<Collection> collections = Collection.where(Collection.COLLECTION_QUERY, format, contentId, false);
    if (collections.size() < 1) {
      LOGGER.warn("Collection id: {} not present in DB", contentId);
    }
    Collection collection = collections.get(0);
    JsonObject returnValue = null;
    if (collection != null) {
      returnValue = new JsonObject(collection.toJson(false));
    }
    closeDefaultDBConn(db);
    return returnValue;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getContentsOfCollection(String collectionId) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);

    List<Map> collectionMeta = db.findAll(Collection.FETCH_RESOURCE_META, collectionId, false);
    if (collectionMeta.size() < 1) {
      LOGGER.warn("Resources for collection : {} not present in DB", collectionId);
    }
    closeDefaultDBConn(db);
    return collectionMeta;
  }

  @Override
  public JsonObject getDeletedCollection(String collectionId) {
    JsonObject returnValue = null;
    try {
      List<Collection> collections = Collection.where(Collection.FETCH_DELETED_QUERY, collectionId, true);
      if (collections.size() < 1) {
        LOGGER.warn("Content id: {} not present in DB", collectionId);
      }
      if (collections.size() > 0) {
        Collection content = collections.get(0);
        if (content != null) {
            returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(content));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to fetch deleted collection from DB : {} error : {}", collectionId, e);
    }
    return returnValue;
  }

  @Override
  public JsonObject getUserCollections(String userId) {
    JsonArray collectionArray = new JsonArray();
    List<Collection> collections = Collection.where(Collection.FETCH_USER_COLLECTIONS, userId, userId, false);
    if(collections != null){
      if (collections.size() < 1) {
        LOGGER.warn("User resources not present in DB for user id: {} not present in DB", userId);
      }
      for(Collection collection : collections){
        collectionArray.add(collection.toJson(false));
      }
    }
    return new JsonObject().put("collections", collectionArray);
  }

  @Override
  public JsonObject getCollectionById(String collectionId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      Collection result = Collection.findById(getPGObject("id", UUID_TYPE, collectionId));
      if (result != null && !result.getBoolean(Collection.IS_DELETED)) {
        returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch collection : {} error : {}", collectionId, e);
    }
    closeDefaultDBConn(db);
    return returnValue;
  }
  
  @Override
  public LazyList<Collection> getCollectionsByCourseId(String courseId) {
    LazyList<Collection> collections = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      collections = Collection.where(Collection.GET_COLLECTIONS_OF_COURSE, courseId, false);
      if (collections.size() < 1) {
        LOGGER.warn("Collections for course: {} not present in DB", courseId);
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch collections for course : {} error : {}", courseId, e);
    }
    closeDefaultDBConn(db);
    return collections;
  }
  
  @Override
  public LazyList<Collection> getCollectionsByLessonId(String lessonId) {
    LazyList<Collection> collections = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      collections = Collection.where(Collection.GET_COLLECTIONS_OF_LESSON, lessonId, false);
      if (collections.size() < 1) {
        LOGGER.warn("Collections for lesson: {} not present in DB", lessonId);
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch collections for lesson : {} error : {}", lessonId, e);
    }
    closeDefaultDBConn(db);
    return collections;
  }
  
  @Override
  public LazyList<Collection> getCollectionsByUnitId(String unitId) {
    LazyList<Collection> collections = null;
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);
    try {
      collections = Collection.where(Collection.GET_COLLECTIONS_OF_UNIT, unitId, false);
      if (collections.size() < 1) {
        LOGGER.warn("Collections for unit: {} not present in DB", unitId);
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch collections for unit : {} error : {}", unitId, e);
    }
    closeDefaultDBConn(db);
    return collections;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Long getUsedByStudentCount(String collectionId) {
    Long count = 0L;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      List countList = db.firstColumn(Collection.GET_STUDENTS_OF_COLLECTION, collectionId);
      if (countList == null || countList.size() < 1) {
        LOGGER.warn("Students for collection : {} not present in DB", collectionId);
        return count;
      }
      count = ((Long) countList.get(0));
    } catch (Exception e) {
      LOGGER.error("Not able to fetch Students count for collection : {} error : {}", collectionId, e);
    } finally {
      closeDefaultDBConn(db);
    }
    return count;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Long getRemixedInCourseCount(String collectionId) {
    Long count = 0L;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      List countList = db.firstColumn(Collection.GET_USED_IN_COURSE_COUNT, collectionId);
      if (countList == null || countList.size() < 1) {
        LOGGER.warn("RemixedInCourse Count for collection : {} not present in DB", collectionId);
        return count;
      }
      count = ((Long) countList.get(0));
    } catch (Exception e) {
      LOGGER.error("Not able to fetch RemixedInCourse count for collection : {} error : {}", collectionId, e);
    } finally {
      closeDefaultDBConn(db);
    }
    return count;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public Long getOATaskCount(String collectionId) {
    Long count = 0L;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      List countList = db.firstColumn(Collection.GET_TASK_COUNTS_OF_OA, collectionId);
      if (countList == null || countList.size() < 1) {
        LOGGER.warn("Task for offline-activity : {} not present in DB", collectionId);
        return count;
      }
      count = ((Long) countList.get(0));
    } catch (Exception e) {
      LOGGER.error("Not able to fetch Task Count for offline-activity : {} error : {}", collectionId, e);
    } finally {
      closeDefaultDBConn(db);
    }
    return count;
  }
  
  @Override
  public JsonObject getItemsOfCourse(String courseId) {
    JsonArray collectionArray = new JsonArray();
    List<Collection> collections = Collection.where(Collection.GET_COLLECTIONS_OF_COURSE, courseId, false);
    if (collections != null){
      if (collections.size() < 1) {
        LOGGER.warn("Collections not present in DB for course id: {} not present in DB", courseId);
      }
      for(Collection collection : collections){
        collectionArray.add(collection.toJson(false));
      }
    }
    return new JsonObject().put("collections", collectionArray);
  }
  
  @Override
  public JsonObject getDeletedItemIdsOfCourse(String courseId) {
    LazyList<Collection> contents = Collection.where(Collection.GET_COLLECTIONS_OF_COURSE, courseId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Collections for course : {} not present in DB", courseId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getDeletedItemIdsOfUnit(String unitId) {
    LazyList<Collection> contents = Collection.where(Collection.GET_COLLECTIONS_OF_UNIT, unitId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Collections for unit : {} not present in DB", unitId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getDeletedItemIdsOfLesson(String lessonId) {
    LazyList<Collection> contents = Collection.where(Collection.GET_COLLECTIONS_OF_LESSON, lessonId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Collections for lesson : {} not present in DB", lessonId);
    }
    JsonObject result = new JsonObject();
    populateResponseWithRelatedIds(contents, result);
    return result;
  }
  
  @Override
  public JsonObject getItemsOfUnit(String unitId) {
    JsonArray collectionArray = new JsonArray();
    List<Collection> collections = Collection.where(Collection.GET_COLLECTIONS_OF_UNIT, unitId, false);
    if(collections != null){
      if (collections.size() < 1) {
        LOGGER.warn("Collections for unit : {} not present in DB", unitId);
      }
      for(Collection collection : collections){
        collectionArray.add(collection.toJson(false));
      }
    }
    return new JsonObject().put("collections", collectionArray);
  }
  
  @Override
  public JsonObject getItemsOfLesson(String lessonId) {
    JsonArray collectionArray = new JsonArray();
    List<Collection> collections = Collection.where(Collection.GET_COLLECTIONS_OF_LESSON, lessonId, false);
    if(collections != null){
      if (collections.size() < 1) {
        LOGGER.warn("Collections for lesson : {} not present in DB", lessonId);
      }
      for(Collection collection : collections){
        collectionArray.add(collection.toJson(false));
      }
    }
    return new JsonObject().put("collections", collectionArray);
  }

  private void populateResponseWithRelatedIds(LazyList<Collection> contents, JsonObject result) {
    JsonArray collectionIds = new JsonArray();
    if (contents.size() > 0) {
      for (Collection content : contents) {
        collectionIds.add(Convert.toString(content.get(EntityAttributeConstants.ID)));
      }
    }
    result.put(IndexerConstants.COLLECTION_IDS, collectionIds);
  }

}
