package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.repositories.entities.User;
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
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    LOGGER.debug("UserRepositoryImpl : getUser: " + userID);

    User result = User.findById(getPGObject("id", UUID_TYPE, userID));
    LOGGER.debug("UserRepositoryImpl : getUser : findById : " + result);

    JsonObject returnValue = null;

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false));
    }
    LOGGER.debug("UserRepositoryImpl : getUser : findById returned: " + returnValue);

    Base.close();
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
