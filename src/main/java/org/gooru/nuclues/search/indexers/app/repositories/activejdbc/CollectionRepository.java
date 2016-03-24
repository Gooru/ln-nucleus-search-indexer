package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface CollectionRepository {

	static CollectionRepository instance() {
		return new CollectionRepositoryImpl();
	}

	JsonObject getCollection(String contentID);

	JsonObject getAssessment(String contentID);

	JsonObject getCollectionByType(String contentID, String format);

	List<Map> getContentsOfCollection(String collectionId);

}
