package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonObject;

public interface IndexRepository {
    
	static IndexRepository instance() {
		return new IndexRepositoryImpl();
	}

	List<Map> getMetadata(String referenceIds);
    JsonObject getCollectionIdsByContentId(String contentId);
}
