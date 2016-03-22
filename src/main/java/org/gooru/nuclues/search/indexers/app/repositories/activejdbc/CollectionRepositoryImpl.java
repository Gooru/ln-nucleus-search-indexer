package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.Set;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.javalite.activejdbc.LazyList;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class CollectionRepositoryImpl implements CollectionRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionRepositoryImpl.class);
	private Collection collection;

	public JsonObject getCollectionByType(String contentID, String format) {
		LazyList<Collection> collections = Collection.where(Collection.AUTHORIZER_QUERY, format, contentID, false);
		// Question should be present in DB
		if (collections.size() < 1) {
			LOGGER.warn("Collection id: {} not present in DB", contentID);
		}
		this.collection = collections.get(0);
		JsonObject returnValue = null;
		Set<String> attributes = Content.attributeNames();
		LOGGER.debug("ContentRepositoryImpl:getQuestion:findById attributes: " + String.join(", ", attributes.toArray(new String[0])));

		if (collection != null) {
			returnValue = new JsonObject(collection.toJson(false, attributes.toArray(new String[0])));
		}
		return returnValue;

	}

	@Override
	public JsonObject getCollection(String contentID) {
		LOGGER.debug("CollectionRepositoryImpl : getCollection : " + contentID);
		Collection result = Collection.findById(getPGObject("id", UUID_TYPE, contentID));
		LOGGER.debug("CollectionRepositoryImpl : getCollection : " + result);

		JsonObject returnValue = null;
		String[] attributes = { "id", "title", "created_at", "updated_at", "creator_id", "original_creator_id", "original_collection_id", "publish_date", "format", "learning_objective",
				"collaborator", "orientation", "grading", "setting", "metadata", "taxonomy", "thumbnail", "visible_on_profile", "course_id", "unit_id", "lesson_id" };
		LOGGER.debug("CollectionRepositoryImpl : getCollection : findById attributes: " + String.join(", ", attributes));

		if (result != null) {
			returnValue = new JsonObject(result.toJson(false, attributes));
		}
		LOGGER.debug("CollectionRepositoryImpl : getCollection : findById returned: " + returnValue);
		return returnValue;

	}

	@Override
	public JsonObject getDeletedCollection(String contentID) {
		LOGGER.debug("CollectionRepositoryImpl : getDeletedCollection : " + contentID);
		// TODO: ...
		return null;
	}

	@Override
	public JsonObject getAssessment(String contentID) {
		LOGGER.debug("CollectionRepositoryImpl : getAssessment : " + contentID);

		Collection result = Collection.findById(getPGObject("id", UUID_TYPE, contentID));
		LOGGER.debug("CollectionRepositoryImpl : getAssessment : " + result);

		JsonObject returnValue = null;
		String[] attributes = { "id", "title", "created_at", "updated_at", "creator_id", "original_creator_id", "original_collection_id", "publish_date", "format", "learning_objective",
				"collaborator", "orientation", "grading", "setting", "metadata", "taxonomy", "thumbnail", "visible_on_profile", "course_id", "unit_id", "lesson_id" };
		LOGGER.debug("CollectionRepositoryImpl : getAssessment : findById attributes: " + String.join(", ", attributes));

		if (result != null) {
			returnValue = new JsonObject(result.toJson(false, attributes));
			LOGGER.debug("CollectionRepositoryImpl : getAssessment : findById returned: " + returnValue);
		}
		LOGGER.debug("CollectionRepositoryImpl : getAssessment : afterAddingContainmentInfo : " + returnValue);

		return returnValue;
	}

	@Override
	public JsonObject getDeletedAssessment(String contentID) {
		LOGGER.debug("CollectionRepositoryImpl : getDeletedAssessment : " + contentID);
		// TODO: ...
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
