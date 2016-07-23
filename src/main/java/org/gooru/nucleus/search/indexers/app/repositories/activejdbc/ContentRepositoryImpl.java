package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.Base;
import org.javalite.common.Convert;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ContentRepositoryImpl implements ContentRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getResource(String contentID) {
    LOGGER.debug("ContentRepositoryImpl:getResource: " + contentID);

    Content result = Content.findById(getPGObject("id", UUID_TYPE, contentID));
   // LOGGER.debug("ContentRepositoryImpl:getResource:findById: " + result);

    JsonObject returnValue = null;
    String collectionId = null;
    String courseId = null;
    if (result != null && !result.getBoolean(Content.IS_DELETED)) {
     returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
     collectionId = returnValue.getString(EntityAttributeConstants.COLLECTION_ID);
     courseId = returnValue.getString(EntityAttributeConstants.COURSE_ID, null);
    }
    if (returnValue != null) {
      // Set collection title
      if (collectionId != null) {
        JsonObject collection = CollectionRepository.instance().getCollection(collectionId);
        if (collection != null) {
          returnValue.put(IndexerConstants.COLLECTION_TITLE, collection.getString(EntityAttributeConstants.TITLE));
        }
      }
      // Set course title
      if (courseId != null) {
        JsonObject courseData = CourseRepository.instance().getCourse(courseId);
        if (courseData != null && !courseData.isEmpty()) {
          returnValue.put(IndexerConstants.RESOURCE_COURSE_ID, courseId);
          returnValue.put(IndexerConstants.RESOURCE_COURSE, courseData.getString(EntityAttributeConstants.TITLE));
        }
      }
    }
    return returnValue;
  }

  @Override
  public JsonObject getQuestion(String contentID) {
    LOGGER.debug("ContentRepositoryImpl:getQuestion: " + contentID);

    Content result = Content.findById(getPGObject("id", UUID_TYPE, contentID));
  //  LOGGER.debug("ContentRepositoryImpl:getResource:findById: " + result);

    JsonObject returnValue = null;
    Set<String> attributes = Content.attributeNames();
   // LOGGER.debug("ContentRepositoryImpl:getQuestion:findById attributes: " + String.join(", ", attributes.toArray(new String[0])));

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false, attributes.toArray(new String[0])));
    }
    LOGGER.debug("ContentRepositoryImpl:getQuestion:findById returned: " + returnValue);

    return returnValue;
  }

  @Override
  public JsonObject getContentByType(String contentId, String contentFormat) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    JsonObject returnValue = null;
    List<Content> contents = Content.where(Content.FETCH_CONTENT_QUERY, contentFormat, contentId, false);
    if (contents.size() < 1) {
      LOGGER.warn("Content id: {} not present in DB", contentId);
    }
    Content content = contents.get(0);
    if (content != null) {
      returnValue = new JsonObject(content.toJson(false));
    }
    Base.close();
    return returnValue;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getCollectionMeta(String parentContentId) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    List<Map> collectionMeta = Base.findAll(Content.FETCH_COLLECTION_META, parentContentId);
    if (collectionMeta.size() < 1) {
      LOGGER.warn("Collections for resource : {} not present in DB", parentContentId);
    }
    Base.close();
    return collectionMeta;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public JsonObject getQuestionAndParentContentIds(String collectionId) {
    List<Map> contents = Base.findAll(Content.FETCH_QUESTION_AND_PARENT_CONTENT_IDS, collectionId);
    if (contents.size() < 1) {
      LOGGER.warn("Resources for collection : {} not present in DB", collectionId);
    }
    JsonObject result = new JsonObject();
    JsonArray questionIds = new JsonArray();
    JsonArray parentContentIds = new JsonArray();

    if (contents.size() > 0) {
      for (Map content : contents) {
        if (content.get(Content.CONTENT_FORMAT) != null && content.get(Content.CONTENT_FORMAT).equals(Content.CONTENT_FORMAT_QUESTION) &&
          content.get(EntityAttributeConstants.ID) != null) {
          questionIds.add(Convert.toString(content.get(EntityAttributeConstants.ID)));
        }
        if (content.get(EntityAttributeConstants.PARENT_CONTENT_ID) != null) {
          parentContentIds.add(Convert.toString(content.get(EntityAttributeConstants.PARENT_CONTENT_ID)));
        }
      }
    }
    result.put(IndexerConstants.PARENT_CONTENT_IDS, parentContentIds);
    result.put(IndexerConstants.QUESTION_IDS, questionIds);
    return result;
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
  public JsonObject getDeletedContent(String contentId) {
    JsonObject returnValue = null;
    List<Content> contents = Content.where(Content.FETCH_DELETED_QUERY, contentId, true);
    if (contents.size() < 1) {
      LOGGER.warn("Content id: {} not present in DB", contentId);
    }
    if(contents.size() > 0){
      Content content = contents.get(0);
      if (content != null) {
        returnValue = new JsonObject(content.toJson(false));
      }
    }
    return returnValue;
  }

  @Override
  public JsonObject getUserResources(String userId) {
    JsonArray contentArray = new JsonArray();
    List<Content> contents = Content.where(Content.FETCH_USER_RESOURCES, userId, userId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("User resources not present in DB for user id: {} not present in DB", userId);
      }
      for(Content content : contents){
        contentArray.add(content.toJson(false));
      }
    }
    return new JsonObject().put("resources", contentArray);
  }

}
