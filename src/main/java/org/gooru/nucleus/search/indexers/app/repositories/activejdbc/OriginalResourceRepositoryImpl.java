package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.repositories.entities.OriginalResource;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class OriginalResourceRepositoryImpl extends BaseIndexRepo implements OriginalResourceRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getResource(String contentID) {
    LOGGER.debug("OriginalResourceRepositoryImpl:getResource: " + contentID);
    OriginalResource result = OriginalResource.findById(getPGObject("id", UUID_TYPE, contentID));

    JsonObject returnValue = null;
    if (result != null && !result.getBoolean(OriginalResource.IS_DELETED)) {
     returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    return returnValue;
  }

  @Override
  public JsonObject getDeletedContent(String contentId) {
    JsonObject returnValue = null;
    try {
      List<OriginalResource> contents = OriginalResource.where(OriginalResource.FETCH_DELETED_QUERY, contentId, true);
      if (contents.size() < 1) {
        LOGGER.warn("Original resource id: {} not present in DB", contentId);
      }
      if (contents.size() > 0) {
        OriginalResource content = contents.get(0);
        if (content != null) {
          returnValue = new JsonObject(content.toJson(false));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to fetch deleted original resource from DB : {} error : {}", contentId, e);
    }
    return returnValue;
  }

  @Override
  public JsonObject getUserOriginalResources(String userId) {
    JsonArray contentArray = new JsonArray();
    List<OriginalResource> contents = OriginalResource.where(OriginalResource.FETCH_USER_ORIGINAL_RESOURCES, userId, false);
    if(contents != null){
      if (contents.size() < 1) {
        LOGGER.warn("User's original resources not present in DB for user id: {} not present in DB", userId);
      }
      for(OriginalResource content : contents){
        contentArray.add(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(content));
      }
    }
    return new JsonObject().put("resources", contentArray);
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
