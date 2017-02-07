package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;

import org.gooru.nucleus.search.indexers.app.repositories.entities.User;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
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
    //LOGGER.debug("UserRepositoryImpl : getUser : findById : " + result);

    JsonObject returnValue = null;
    
    if (result != null) {
      returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    //LOGGER.debug("UserRepositoryImpl : getUser : findById returned: " + returnValue);

    closeDBConn(db);
    return returnValue;
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
