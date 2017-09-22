package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class UnitEo {

  private JsonObject unit = null;

  public UnitEo() {
    this.unit = new JsonObject();
  }

  public JsonObject getUnitJson() {
    return !unit.isEmpty() ? unit : null;
  }
  
  public String getId() {
    return unit.getString("id", null);
  }

  public void setId(String id) {
    unit = JsonUtil.set(unit, "id", id);
  }
  
  public String getTitle() {
    return unit.getString("title", null);
  }

  public void setTitle(String title) {
    unit = JsonUtil.set(unit, "title", title);
  }
  
}
