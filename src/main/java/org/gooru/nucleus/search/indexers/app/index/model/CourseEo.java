package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class CourseEo {

  private JsonObject course = null;

  public CourseEo() {
    this.course = new JsonObject();
  }

  public JsonObject getCourseJson() {
    return !course.isEmpty() ? course : null;
  }
  
  public String getId() {
    return course.getString("id", null);
  }

  public void setId(String id) {
    course = JsonUtil.set(course, "id", id);
  }
  
  public String getTitle() {
    return course.getString("title", null);
  }

  public void setTitle(String title) {
    course = JsonUtil.set(course, "title", title);
  }
  
}
