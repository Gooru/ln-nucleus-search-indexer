package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.repositories.entities.User;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class UserRepositoryImpl extends BaseIndexRepo implements UserRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);
  private static final String UUID_TYPE = "uuid";

  @Override
  public JsonObject getUser(String userID) {
    DB db = getDefaultDataSourceDBConnection();
    openDefaultDBConnection(db);

    User result = User.findById(getPGObject("id", UUID_TYPE, userID));
    JsonObject returnValue = null;
    
    if (result != null) {
      returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result));
    }
    closeDefaultDBConn(db);
    return returnValue;
  }

}
