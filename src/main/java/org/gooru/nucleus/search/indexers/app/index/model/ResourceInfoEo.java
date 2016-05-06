package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class ResourceInfoEo {

  private JsonObject resourceInfo = null;
  
  public ResourceInfoEo() {
    this.resourceInfo = new JsonObject();
  }

  public JsonObject getResourceInfo() {
    return resourceInfo;
  }

  public String getText() {
    return resourceInfo.getString("text", null);
  }

  public void setText(String text) {
    this.resourceInfo = JsonUtil.set(resourceInfo, "text", text);
  }
}
