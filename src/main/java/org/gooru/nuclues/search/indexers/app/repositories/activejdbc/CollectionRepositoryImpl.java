package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.javalite.activejdbc.Base;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class CollectionRepositoryImpl implements CollectionRepository {

	  private static final Logger LOGGER = LoggerFactory.getLogger(CollectionRepositoryImpl.class);

	  @Override
	  public JsonObject getCollection(String contentID) {
	    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
	    LOGGER.debug("CollectionRepositoryImpl : getCollection : " + contentID);

	    Collection result = Collection.findById(getPGObject("id", UUID_TYPE, contentID));
	    LOGGER.debug("CollectionRepositoryImpl : getCollection : " + result);

	    JsonObject returnValue = null;
	    String[] attributes =  {"id", "title", "created_at", "updated_at", "creator_id", "original_creator_id", "original_collection_id",
	                            "publish_date", "format", "learning_objective", "collaborator", "orientation", "grading", "setting",
	                            "metadata", "taxonomy", "thumbnail", "visible_on_profile", "course_id", "unit_id", "lesson_id" };
	    LOGGER.debug("CollectionRepositoryImpl : getCollection : findById attributes: " + String.join(", ", attributes) );

	    if (result != null) {
	      returnValue =  new JsonObject(result.toJson(false,  attributes ));
	    }
	    LOGGER.debug("CollectionRepositoryImpl : getCollection : findById returned: " + returnValue);

	    Base.close();
	    return returnValue;
	  }

	  @Override
	  public JsonObject getDeletedCollection(String contentID) {
	    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
	    LOGGER.debug("CollectionRepositoryImpl : getDeletedCollection : " + contentID);
	    // TODO: ...
	    Base.close();
	    return null;
	  }

	  @Override
	  public JsonObject getAssessment(String contentID) {
	    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
	    LOGGER.debug("CollectionRepositoryImpl : getAssessment : " + contentID);

	    Collection result = Collection.findById(getPGObject("id", UUID_TYPE, contentID));
	    LOGGER.debug("CollectionRepositoryImpl : getAssessment : " + result);

	    JsonObject returnValue = null;
	    String[] attributes =  {"id", "title", "created_at", "updated_at", "creator_id", "original_creator_id", "original_collection_id",
	                            "publish_date", "format", "learning_objective", "collaborator", "orientation", "grading", "setting",
	                            "metadata", "taxonomy", "thumbnail", "visible_on_profile", "course_id", "unit_id", "lesson_id" };
	    LOGGER.debug("CollectionRepositoryImpl : getAssessment : findById attributes: " + String.join(", ", attributes) );

	    if (result != null) {
	      returnValue =  new JsonObject(result.toJson(false,  attributes ));
	      LOGGER.debug("CollectionRepositoryImpl : getAssessment : findById returned: " + returnValue);
	    }
	    LOGGER.debug("CollectionRepositoryImpl : getAssessment : afterAddingContainmentInfo : " + returnValue);

	    Base.close();
	    return returnValue;
	  }

	  @Override
	  public JsonObject getDeletedAssessment(String contentID) {
	    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
	    LOGGER.debug("CollectionRepositoryImpl : getDeletedAssessment : " + contentID);
	    // TODO: ...
	    Base.close();
	    return null;
	  }


	  private static final String UUID_TYPE = "uuid";

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
