package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CollectionRepositoryImpl implements CollectionRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectionRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @SuppressWarnings("rawtypes")
  @Override
  public JsonObject getCollection(String contentID) {
    LOGGER.debug("CollectionRepositoryImpl : getCollection : " + contentID);
    Collection result = Collection.findById(getPGObject("id", UUID_TYPE, contentID));
    LOGGER.debug("CollectionRepositoryImpl : getCollection : " + result);

    JsonObject returnValue = null;
    String courseId = null;

    if (result != null && !result.getBoolean(Collection.IS_DELETED)) {
      returnValue = new JsonObject(result.toJson(false));
      courseId = returnValue.getString(EntityAttributeConstants.COURSE_ID);
    }
    
    // Set course title
    if (courseId != null && returnValue != null) {
      List<Map> courseData = CourseRepository.instance().getCourse(courseId);
      if (courseData != null && courseData.size() > 0) {
        returnValue.put(IndexerConstants.COURSE_TITLE, courseData.get(0).get(EntityAttributeConstants.TITLE).toString());
      }
    }
    
    LOGGER.debug("CollectionRepositoryImpl : getCollection : findById returned: " + returnValue);
    return returnValue;
  }

  @Override
  public JsonObject getAssessment(String contentID) {
    LOGGER.debug("CollectionRepositoryImpl : getAssessment : " + contentID);

    Collection result = Collection.findById(getPGObject("id", UUID_TYPE, contentID));
    LOGGER.debug("CollectionRepositoryImpl : getAssessment : " + result);

    JsonObject returnValue = null;
    String[] attributes =
      {"id", "title", "created_at", "updated_at", "creator_id", "original_creator_id", "original_collection_id", "publish_date", "format",
        "learning_objective", "collaborator", "orientation", "grading", "setting", "metadata", "taxonomy", "thumbnail", "visible_on_profile",
        "course_id", "unit_id", "lesson_id"};
    LOGGER.debug("CollectionRepositoryImpl : getAssessment : findById attributes: " + String.join(", ", attributes));

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false, attributes));
      LOGGER.debug("CollectionRepositoryImpl : getAssessment : findById returned: " + returnValue);
    }
    LOGGER.debug("CollectionRepositoryImpl : getAssessment : afterAddingContainmentInfo : " + returnValue);

    return returnValue;
  }

  @Override
  public JsonObject getCollectionByType(String contentID, String format) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    LazyList<Collection> collections = Collection.where(Collection.COLLECTION_QUERY, format, contentID, false);
    if (collections.size() < 1) {
      LOGGER.warn("Collection id: {} not present in DB", contentID);
    }
    Collection collection = collections.get(0);
    JsonObject returnValue = null;
    if (collection != null) {
      returnValue = new JsonObject(collection.toJson(false));
    }
    Base.close();
    return returnValue;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getContentsOfCollection(String collectionId) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    List<Map> collectionMeta = Base.findAll(Collection.FETCH_RESOURCE_META, collectionId);
    if (collectionMeta.size() < 1) {
      LOGGER.warn("Resources for collection : {} not present in DB", collectionId);
    }
    Base.close();
    return collectionMeta;
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
  public JsonObject getDeletedCollection(String collectionId) {
    JsonObject returnValue = null;
    List<Collection> collections = Collection.where(Collection.FETCH_DELETED_QUERY, collectionId, true);
    if (collections.size() < 1) {
      LOGGER.warn("Content id: {} not present in DB", collectionId);
    }
    if(collections.size() > 0){
      Collection content = collections.get(0);
      if (content != null) {
        returnValue = new JsonObject(content.toJson(false));
      }
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

}
