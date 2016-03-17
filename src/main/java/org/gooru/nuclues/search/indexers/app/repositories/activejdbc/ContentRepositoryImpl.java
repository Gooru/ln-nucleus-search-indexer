package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.Set;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.javalite.activejdbc.Base;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class ContentRepositoryImpl implements ContentRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentRepositoryImpl.class);

	@Override
	public JsonObject getResource(String contentID) {
		Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
		LOGGER.debug("ContentRepositoryImpl:getResource: " + contentID);

		Content result = Content.findById(getPGObject("id", UUID_TYPE, contentID));
		LOGGER.debug("ContentRepositoryImpl:getResource:findById: " + result);

		JsonObject returnValue = null;
		String[] attributes = { "id", "title", "description", "url", "created_at", "updated_at", "creator_id", "original_creator_id", "original_content_id", "narration", "content_format",
				"content_subformat", "metadata", "taxonomy", "depth_of_knowledge", "thumbnail", "course_id", "unit_id", "lesson_id", "collection_id", "sequence_id", "is_copyright_owner",
				"copyright_owner", "visible_on_profile", "info", "display_guide", "accessibility" };
		LOGGER.debug("ContentRepositoryImpl:getResource:findById attributes: " + String.join(", ", attributes));

		if (result != null) {
			returnValue = new JsonObject(result.toJson(false, attributes));
		}
		LOGGER.debug("ContentRepositoryImpl:getResource:findById returned: " + returnValue);

		Base.close();
		return returnValue;
	}

	@Override
	public JsonObject getDeletedResource(String contentID) {
		Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
		LOGGER.debug("ContentRepositoryImpl:getDeletedResource: " + contentID);

		Content result = Content.findById(getPGObject("id", UUID_TYPE, contentID));
		LOGGER.debug("ContentRepositoryImpl:getDeletedResource:findById: " + result);

		JsonObject returnValue = null;
		String[] attributes = { "id", "title", "description", "url", "created_at", "updated_at", "creator_id", "original_creator_id", "original_content_id", "content_format", "content_subformat",
				"course_id", "unit_id", "lesson_id", "collection_id", "sequence_id", "is_copyright_owner", "copyright_owner" };
		LOGGER.debug("ContentRepositoryImpl:getDeletedResource:findById attributes: " + String.join(", ", attributes));

		if (result != null) {
			returnValue = new JsonObject(result.toJson(false, attributes));
		}
		LOGGER.debug("ContentRepositoryImpl:getDeletedResource:findById returned: " + returnValue);

		Base.close();
		return returnValue;
	}

	@Override
	public JsonObject getQuestion(String contentID) {
		Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
		LOGGER.debug("ContentRepositoryImpl:getQuestion: " + contentID);

		Content result = Content.findById(getPGObject("id", UUID_TYPE, contentID));
		LOGGER.debug("ContentRepositoryImpl:getResource:findById: " + result);

		JsonObject returnValue = null;
		Set<String> attributes = Content.attributeNames();
		LOGGER.debug("ContentRepositoryImpl:getQuestion:findById attributes: " + String.join(", ", attributes.toArray(new String[0])));

		if (result != null) {
			returnValue = new JsonObject(result.toJson(false, attributes.toArray(new String[0])));
		}
		LOGGER.debug("ContentRepositoryImpl:getQuestion:findById returned: " + returnValue);

		Base.close();
		return returnValue;
	}

	@Override
	public JsonObject getDeletedQuestion(String contentID) {
		Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
		LOGGER.debug("ContentRepositoryImpl:getDeletedQuestion: " + contentID);

		Content result = Content.findById(getPGObject("id", UUID_TYPE, contentID));
		LOGGER.debug("ContentRepositoryImpl:getDeletedResource:findById: " + result);

		JsonObject returnValue = null;
		String[] attributes = { "id", "title", "description", "url", "short_title", "created_at", "updated_at", "creator_id", "original_creator_id", "original_content_id", "content_format",
				"content_subformat", "course_id", "unit_id", "lesson_id", "collection_id", "sequence_id" };
		LOGGER.debug("ContentRepositoryImpl:getDeletedQuestion:findById attributes: " + String.join(", ", attributes));

		if (result != null) {
			returnValue = new JsonObject(result.toJson(false, attributes));
		}
		LOGGER.debug("ContentRepositoryImpl:getDeletedQuestion:findById returned: " + returnValue);

		Base.close();
		return returnValue;
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
