package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.repositories.entities.User;
import org.javalite.activejdbc.DB;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class UserRepositoryImpl extends BaseIndexRepo implements UserRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getUser(String userID) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);

    LOGGER.debug("UserRepositoryImpl : getUser: " + userID);

    User result = User.findById(getPGObject("id", UUID_TYPE, userID));
    LOGGER.debug("UserRepositoryImpl : getUser : findById : " + result);

    JsonObject returnValue = null;
    String[] attributes =
      {"id", "firstname", "lastname", "email_id", "parent_user_id", "user_category", "created_at", "updated_at", "last_login", "birth_date", "grade",
        "thumbnail_path", "gender", "school_id", "school_district_id", "country_id", "state_id"};

    LOGGER.debug("UserRepositoryImpl : getUser : findById attributes: " + String.join(", ", attributes));

    if (result != null) {
      returnValue = new JsonObject(result.toJson(false, attributes));
    }
    LOGGER.debug("UserRepositoryImpl : getUser : findById returned: " + returnValue);

    closeDBConn(db);
    return returnValue;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getUserDetails(String userID) {
    try{
      DB db = getDefaultDataSourceDBConnection();
      openConnection(db);

      LOGGER.debug("UserRepositoryImpl : getUserDetails: " + userID);
      List<Map> userData = db.findAll(User.GET_USER, userID);
      if (userData.size() < 1) {
        LOGGER.warn("User id: {} not present in DB", userID);
      }
      closeDBConn(db);
      return userData;
    }
    catch(Exception e){
      LOGGER.error("Fetch user details failed :", e);
    }
    return null;

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
