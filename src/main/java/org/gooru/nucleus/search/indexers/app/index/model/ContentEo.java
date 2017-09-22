package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class ContentEo {

	private JsonObject content = null;

	public ContentEo() {
		this.content = new JsonObject();
	}

	public JsonObject getContentJson() {
		return !content.isEmpty() ? content : null;
	}

	public String getId() {
		return content.getString("id", null);
	}

	public void setId(String id) {
		content = JsonUtil.set(content, "id", id);
	}

	public String getTitle() {
		return content.getString("title", null);
	}

	public void setTitle(String title) {
		content = JsonUtil.set(content, "title", title);
	}

}
