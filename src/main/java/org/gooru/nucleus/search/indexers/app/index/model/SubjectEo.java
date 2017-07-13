package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class SubjectEo {

  private JsonObject subject = null;

  public SubjectEo() {
    this.subject = new JsonObject();
  }

  public JsonObject getSubjectJson() {
    return !subject.isEmpty() ? subject : null;
  }
  
  public String getId() {
    return subject.getString("id", null);
  }

  public void setId(String id) {
    subject = JsonUtil.set(subject, "id", id);
  }
  
  public String getTitle() {
    return subject.getString("title", null);
  }

  public void setTitle(String title) {
    subject = JsonUtil.set(subject, "title", title);
  }
  
  public String getSubjectClassification() {
    return subject.getString("subjectClassification", null);
  }

  public void setSubjectClassification(String subjectClassification) {
    subject = JsonUtil.set(subject, "subjectClassification", subjectClassification);
  }

}
