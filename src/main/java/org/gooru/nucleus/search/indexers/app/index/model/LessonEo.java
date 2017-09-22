package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class LessonEo {

  private JsonObject lesson = null;

  public LessonEo() {
    this.lesson = new JsonObject();
  }

  public JsonObject getLessonJson() {
    return !lesson.isEmpty() ? lesson : null;
  }
  
  public String getId() {
    return lesson.getString("id", null);
  }

  public void setId(String id) {
    lesson = JsonUtil.set(lesson, "id", id);
  }
  
  public String getTitle() {
    return lesson.getString("title", null);
  }

  public void setTitle(String title) {
    lesson = JsonUtil.set(lesson, "title", title);
  }
  
}
