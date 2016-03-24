package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface ContentRepository {

	static ContentRepository instance() {
		return new ContentRepositoryImpl();
	}

	JsonObject getResource(String contentID);

	JsonObject getQuestion(String contentID);

	JsonObject getContentByType(String contentId, String contentFormat);

	List<Map> getCollectionMeta(String parentContentId);

}
