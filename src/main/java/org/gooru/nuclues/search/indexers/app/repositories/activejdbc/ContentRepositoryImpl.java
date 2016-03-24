package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
	public JsonObject getContentByType(String contentId, String contentFormat) {
		Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
		JsonObject returnValue = null;
		List<Content> contents = Content.where(Content.FETCH_CONTENT_QUERY, contentFormat, contentId, false);
		if (contents.size() < 1) {
			LOGGER.warn("Content id: {} not present in DB", contentId);
		}
		Content content = contents.get(0);
		if (content != null) {
			returnValue = new JsonObject(content.toJson(false));
		}
		Base.close();
		return returnValue;
	}
	
	@Override
	public List<Map> getCollectionMeta(String parentContentId) {
		Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
		List<Map> collectionMeta = Base.findAll(Content.FETCH_COLLECTION_META, parentContentId);
		if (collectionMeta.size() < 1) {
			LOGGER.warn("Collections for resource : {} not present in DB", parentContentId);
		}
		Base.close();
		return  collectionMeta;
	}
	
	@Override
	public JsonObject getResource(String contentID) {
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

		return returnValue;
	}

	@Override
	public JsonObject getQuestion(String contentID) {
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
