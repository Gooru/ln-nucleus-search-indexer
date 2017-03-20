package org.gooru.nucleus.search.indexers.app.index.model;

import java.io.Serializable;
import java.util.Date;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class CrosswalkEio implements Serializable {

  private static final long serialVersionUID = -7695456210960325029L;
  private JsonObject crosswalk = null;

  public CrosswalkEio() {
    this.crosswalk = new JsonObject();
  }

  public JsonObject getCrosswalkJson() {
    return this.crosswalk;
  }

  public String getId() {
    return crosswalk.getString("id", null);
  }

  public void setId(String id) {
    crosswalk = JsonUtil.set(crosswalk, "id", id);
  }
  
  public String getIndexType() {
    return crosswalk.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    crosswalk = JsonUtil.set(crosswalk, "indexType", indexType);
  }
  
  public String getDisplayCode() {
    return crosswalk.getString("displayCode", null);
  }

  public void setDisplayCode(String displayCode) {
    crosswalk = JsonUtil.set(crosswalk, "displayCode", displayCode);
  }
  
  public String getGdtCode() {
    return crosswalk.getString("gdtCode", null);
  }

  public void setGdtCode(String gdtCode) {
    crosswalk = JsonUtil.set(crosswalk, "gdtCode", gdtCode);
  }
  
  public String getEquivalentCompetencies(String key) {
    return crosswalk.getString(key, null);
  }

  public void setEquivalentCompetencies(JsonObject data) {
    crosswalk = JsonUtil.set(crosswalk, "equivalentCompetencies", data);
  }

  public String getIndexUpdatedTime() {
    return crosswalk.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    this.crosswalk = JsonUtil.set(crosswalk, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }
}
