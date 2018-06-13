package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;

import org.gooru.nucleus.search.indexers.app.repositories.entities.IndexerJobStatus;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonObject;

public interface IndexTrackerRepository {

    static IndexTrackerRepository instance() {
        return new IndexTrackerRepositoryImpl();
    }

    void saveDeletedResource(String id, JsonObject data, List<String> attributes);

    void saveDeletedCollection(String id, JsonObject request, List<String> insertCollectionAllowedFields);

    LazyList<IndexerJobStatus> getJobStatus(String jobKey);

    void saveJobStatus(String jobKey, String status);
}
