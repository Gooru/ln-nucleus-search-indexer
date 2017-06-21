package org.gooru.nucleus.search.indexers.app.index.model;

import java.io.Serializable;
import java.util.Date;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CrosswalkEio implements Serializable {

  private static final long serialVersionUID = -7695456210960325022L;
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
  
  public String getCodeType() {
    return crosswalk.getString("codeType", null);
  }

  public void setCodeType(String codeType) {
    crosswalk = JsonUtil.set(crosswalk, "codeType", codeType);
  }
  
  public String getCode() {
    return crosswalk.getString("code", null);
  }

  public void setCode(String code) {
    crosswalk = JsonUtil.set(crosswalk, "code", code);
  }

  public String getIndexUpdatedTime() {
    return crosswalk.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    this.crosswalk = JsonUtil.set(crosswalk, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }
  
  public JsonArray getCrosswalkCodes(String key) {
    return crosswalk.getJsonArray(key);
  }

  public void setCrosswalkCodes(JsonArray crosswalkCodes) {
    crosswalk = JsonUtil.set(crosswalk, "crosswalkCodes", crosswalkCodes);
  }
  
}
