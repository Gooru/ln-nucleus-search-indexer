package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class ContentInfoEio {

  private JsonObject contentInfo = null;

  public ContentInfoEio() {
    this.contentInfo = new JsonObject();
  }

  public JsonObject getContentInfoJson() {
    return this.contentInfo;
  }

  public String getId() {
    return contentInfo.getString("id", null);
  }

  public void setId(String id) {
    contentInfo = JsonUtil.set(contentInfo, "id", id);
  }
  
  public String getContentFormat() {
    return contentInfo.getString("contentFormat", null);
  }

  public void setContentFormat(String contentFormat) {
    contentInfo = JsonUtil.set(contentInfo, "contentFormat", contentFormat);
  }
  
  public JsonObject getResourceInfo() {
    return contentInfo.getJsonObject("resourceInfo", null);
  }

  public void setResourceInfo(JsonObject resourceInfo) {
    contentInfo = JsonUtil.set(contentInfo, "resourceInfo", resourceInfo);
  }
  
  public JsonObject getStatistics() {
    return contentInfo.getJsonObject("statistics", null);
  }

  public void setStatistics(JsonObject statistics) {
    contentInfo = JsonUtil.set(contentInfo, "statistics", statistics);
  }

}
