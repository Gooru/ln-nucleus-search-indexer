package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class CollectionEo {

	private JsonObject collection = null;

	public CollectionEo() {
		this.collection = new JsonObject();
	}

	public JsonObject getCollectionJson() {
		return !collection.isEmpty() ? collection : null;
	}

	public String getId() {
		return collection.getString("id", null);
	}

	public void setId(String id) {
		collection = JsonUtil.set(collection, "id", id);
	}

	public String getTitle() {
		return collection.getString("title", null);
	}

	public void setTitle(String title) {
		collection = JsonUtil.set(collection, "title", title);
	}

}
