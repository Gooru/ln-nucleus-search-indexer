package org.gooru.nucleus.search.indexers.app.services;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchClient;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nuclues.search.indexers.app.builders.EsIndexSrcBuilder;
import org.gooru.nuclues.search.indexers.app.utils.IdIterator;

import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 * 
 */
public class EsIndexServiceImpl implements IndexService {

	@Override
	public void deleteDocuments(String indexableIds, String indexName, String type) {
		for (String key : indexableIds.split(",")) {
			getClient().prepareDelete(indexName, type, key).execute().actionGet();
		}
	}

	@Override
	public void refreshIndex(String indexName) {
		getClient().admin().indices().refresh(new RefreshRequest(indexName));
	}

	@Override
	public void indexDocuments(String indexableIds, String indexName, String typeName, JsonObject body) {

		new IdIterator(indexableIds) {
			@Override
			public void execute(String indexableId) {
				if ((typeName.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE) || typeName.equalsIgnoreCase(IndexerConstants.TYPE_SCOLLECTION))) {
					if (!body.isEmpty()) {
						getClient().prepareIndex(indexName, typeName, indexableId).setSource(EsIndexSrcBuilder.get(typeName).buildSource(body)).execute();
					}
				}
			}
		};

	}

	private Client getClient() {
		return ElasticSearchClient.getFactory().getClient();
	}

}
