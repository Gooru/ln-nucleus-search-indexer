package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

public class AnswerEo {

  private JsonObject answer;

  public AnswerEo() {
    this.answer = new JsonObject();
  }

  public JsonObject getAnswer() {
    return answer;
  }

  public String getAnswerText() {
    return answer.getString("answerText", null);
  }

  public void setAnswerText(String answerText) {
    this.answer = JsonUtil.set(answer, "answerText", answerText);
  }

}
