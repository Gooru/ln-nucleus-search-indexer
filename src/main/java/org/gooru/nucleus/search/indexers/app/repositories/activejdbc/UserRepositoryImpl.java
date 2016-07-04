package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.User;
import org.gooru.nucleus.search.indexers.app.repositories.entities.UserIdentity;
import org.gooru.nucleus.search.indexers.app.repositories.entities.UserPreference;
import org.javalite.activejdbc.Base;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class UserRepositoryImpl implements UserRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getUser(String userID) {
    LOGGER.debug("UserRepositoryImpl : getUser: " + userID);

    User result = User.findById(getPGObject("id", UUID_TYPE, userID));
    LOGGER.debug("UserRepositoryImpl : getUser : findById : " + result);

    JsonObject returnValue = null;

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false));
    }

    // Set username
    JsonObject identityData = getUserIdentity(userID);
    if (identityData != null && !identityData.isEmpty()) {
      returnValue.put(IndexerConstants.USERNAME, identityData.getString(EntityAttributeConstants.USERNAME));
    }
    // Set user preference
    JsonObject preferenceData = getUserIdentity(userID);
    if (preferenceData != null && !preferenceData.isEmpty()) {
      returnValue.put(IndexerConstants.PROFILE_VISIBILITY, preferenceData.getBoolean(EntityAttributeConstants.PROFILE_VISIBILITY));
      //returnValue.put(IndexerConstants.STANDARD_PREFERENCE, preferenceData.getJsonObject(EntityAttributeConstants.STANDARD_PREFERENCE));
    }
    LOGGER.debug("UserRepositoryImpl : getUser : findById returned: " + returnValue);

    return returnValue;
  }
  
  @Override
  public JsonObject getUserIdentity(String userID) {
    LOGGER.debug("UserRepositoryImpl : getUserIdentity: " + userID);

    UserIdentity result = UserIdentity.findById(getPGObject("id", UUID_TYPE, userID));
    LOGGER.debug("UserRepositoryImpl : getUserIdentity : findById : " + result);

    JsonObject returnValue = null;

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false));
    }
    
    LOGGER.debug("UserRepositoryImpl : getUserIdentity : findById returned: " + returnValue);

    return returnValue;
  }
  
  @Override
  public JsonObject getUserPreference(String userID) {
    LOGGER.debug("UserRepositoryImpl : getUserPreference: " + userID);

    UserPreference result = UserPreference.findById(getPGObject("id", UUID_TYPE, userID));
    LOGGER.debug("UserRepositoryImpl : getUserPreference : findById : " + result);

    JsonObject returnValue = null;

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false));
    }
    
    LOGGER.debug("UserRepositoryImpl : getUserPreference : findById returned: " + returnValue);

    return returnValue;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getUserDetails(String userID) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    LOGGER.debug("UserRepositoryImpl : getUserDetails: " + userID);
    List<Map> userData = Base.findAll(User.GET_USER, userID);
    if (userData.size() < 1) {
      LOGGER.warn("User id: {} not present in DB", userID);
    }
    Base.close();
    return userData;

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
