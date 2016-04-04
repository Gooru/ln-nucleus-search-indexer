package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class AnswerEo {

	private JsonObject answer;

	public AnswerEo() {
		this.answer = new JsonObject();
	}

	public JsonObject getAnswer() {
		return answer;
	} 
	
	public void setAnswerText(String answerText) {
		this.answer = JsonUtil.set(answer, "answerText", answerText);
	}

	public String getAnswerText() {
		return answer.getString("answerText", null);
	}

}
