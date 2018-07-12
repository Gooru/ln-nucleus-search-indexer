package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.javalite.common.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ContentRepositoryImpl extends BaseIndexRepo implements ContentRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getQuestionOrResourceReference(String contentID) {
    LOGGER.debug("ContentRepositoryImpl:getQuestionOrCopiedResource: " + contentID);

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
          returnValue.put(EntityAttributeConstants.FORMAT, collection.getString(EntityAttributeConstants.FORMAT));
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
 //  LOGGER.debug("ContentRepositoryImpl:getResource:findById returned: " + returnValue);
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
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);

    JsonObject returnValue = null;
    List<Content> contents = Content.where(Content.FETCH_CONTENT_QUERY, contentFormat, contentId, false);
    if (contents.size() < 1) {
      LOGGER.warn("Content id: {} not present in DB", contentId);
    }
    Content content = contents.get(0);
    if (content != null) {
      returnValue = new JsonObject(content.toJson(false));
    }
    closeDefaultDBConn(db);
    return returnValue;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getCollectionMeta(String parentContentId) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);
    List<Map> collectionMeta = db.findAll(Content.FETCH_COLLECTION_META, parentContentId);
    if (collectionMeta.size() < 1) {
      LOGGER.warn("Collections for resource : {} not present in DB", parentContentId);
    }
    closeDefaultDBConn(db);
    return collectionMeta;
  }
  
  @Override
  public JsonObject getQuestionAndOriginalResourceIds(String collectionId) {
    LazyList<Content> contents = Content.find(Content.FETCH_QUESTION_AND_ORIGINAL_RESOURCE_IDS, collectionId);
    if (contents.size() < 1) {
      LOGGER.warn("Resources for collection : {} not present in DB", collectionId);
    }
    JsonObject result = new JsonObject();
    JsonArray questionIds = new JsonArray();
    JsonArray originalContentIds = new JsonArray();
    JsonArray copiedResourceIds = new JsonArray();
    
    if (contents.size() > 0) {
      for (Content content : contents) {
        if (content.get(Content.CONTENT_FORMAT) != null && content.get(EntityAttributeConstants.ID) != null) {
           if (content.get(Content.CONTENT_FORMAT).equals(Content.CONTENT_FORMAT_QUESTION))
              questionIds.add(Convert.toString(content.get(EntityAttributeConstants.ID)));
           if (content.get(Content.CONTENT_FORMAT).equals(Content.CONTENT_FORMAT_RESOURCE))
              copiedResourceIds.add(Convert.toString(content.get(EntityAttributeConstants.ID)));
        } else if (content.get(EntityAttributeConstants.ORIGINAL_CONTENT_ID) != null) {
          originalContentIds.add(Convert.toString(content.get(EntityAttributeConstants.ORIGINAL_CONTENT_ID)));
        }
      }
    }
    result.put(IndexerConstants.ORIGINAL_CONTENT_IDS, originalContentIds);
    result.put(IndexerConstants.RESOURCE_REFERENCES_IDS, copiedResourceIds);
    result.put(IndexerConstants.QUESTION_IDS, questionIds);
    return result;
  }

  @Override
  public JsonObject getDeletedContent(String contentId) {
    JsonObject returnValue = null;
    try {
      List<Content> contents = Content.where(Content.FETCH_DELETED_QUERY, contentId, true);
      if (contents.size() < 1) {
        LOGGER.warn("Content id: {} not present in DB", contentId);
      }
      if (contents.size() > 0) {
        Content content = contents.get(0);
        if (content != null) {
            returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(content));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to fetch deleted content from DB : {} error : {}", contentId, e);
    }
    return returnValue;
  }

  @Override
  public JsonObject getUserQuestions(String userId) {
    JsonArray contentArray = new JsonArray();
    List<Content> contents = Content.where(Content.FETCH_USER_CONTENTS, Content.CONTENT_FORMAT_QUESTION, userId, userId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("User questions not present in DB for user id: {}", userId);
      }
      for(Content content : contents){
        contentArray.add(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(content));
      }
    }
    return new JsonObject().put("questions", contentArray);
  }
  
  @Override
  public JsonObject getQuestionById(String contentId) {
    JsonObject returnValue = null;
    DB db = getDefaultDataSourceDBConnection();
    try {
      openDefaultDBConnection(db);
      Content result = Content.findById(getPGObject("id", UUID_TYPE, contentId));
      if (result != null && !result.getBoolean(Content.IS_DELETED)) {
        returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
      }
    } catch (Exception e) {
      LOGGER.error("Not able to fetch content : {} error : {}", contentId, e);
    }
    closeDefaultDBConn(db);
    return returnValue;
  }
  
  @Override
    public Boolean isOEExistInCollection(String collectionId) {
        Boolean hasOEQuestion = false;
        DB db = getDefaultDataSourceDBConnection();
        try {
            openDefaultDBConnection(db);
            List<Content> contents = Content.where(Content.GET_OE_QUESTION_OF_COLLECTION, collectionId);
            if (contents != null && contents.size() > 0) {
                hasOEQuestion = true;
            }
        } catch (Exception e) {
            LOGGER.error("Not able to fetch content for collection : {} error : {}", collectionId, e);
        }
        closeDefaultDBConn(db);
        return hasOEQuestion;
    }
 
  @Override
  public JsonObject getUserCopiedResources(String userId) {
    JsonArray contentArray = new JsonArray();
    List<Content> contents = Content.where(Content.FETCH_USER_CONTENTS, Content.CONTENT_FORMAT_RESOURCE, userId, userId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("User copied resources not present in DB for user id: {}", userId);
      }
      for(Content content : contents){
        contentArray.add(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(content));
      }
    }
    return new JsonObject().put(IndexerConstants.RESOURCE_REFERENCES, contentArray);
  }
}
