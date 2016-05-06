package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

public class QuestionEo {

  private JsonObject question;

  public QuestionEo() {
    this.question = new JsonObject();
  }

  public JsonObject getQuestion() {
    return question;
  }

  public JsonObject getAnswer() {
    return question.getJsonObject("answer", null);
  }

  public void setAnswer(JsonObject answer) {
    this.question = JsonUtil.set(question, "answer", answer);
  }

  public String getExplanation() {
    return question.getString("explanation", null);
  }

  public void setExplanation(String explanation) {
    this.question = JsonUtil.set(question, "explanation", explanation);
  }

  public JsonObject getHint() {
    return question.getJsonObject("hint", null);
  }

  public void setHint(JsonObject hint) {
    this.question = JsonUtil.set(question, "hint", hint);
  }
  
  public void setQuestionText(String questionText){
    this.question = JsonUtil.set(question, "questionText", questionText);
  }
  
  public String getQuestionText(){
    return question.getString("questionText", null);
  }

}
