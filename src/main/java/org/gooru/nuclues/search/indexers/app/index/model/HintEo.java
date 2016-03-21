package org.gooru.nuclues.search.indexers.app.index.model;

import org.gooru.nuclues.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class HintEo {

	private JsonObject hint;

	public HintEo() {
		this.hint = new JsonObject();
	}

	public JsonObject getHint() {
		return hint;
	}

	public void setHintText(String hintText) {
		this.hint = JsonUtil.set(hint, "hintText", hintText);
	}

	public String getHintText() {
		return hint.getString("hintText", null);
	}

	public void setHintCount(Integer hintCount) {
		this.hint = JsonUtil.set(hint, "hintCount", hintCount);
	}

	public Integer getHintCount() {
		return hint.getInteger("hintCount", 0);
	}

}
