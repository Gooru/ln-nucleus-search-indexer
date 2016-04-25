package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class LicenseEo {

  private JsonObject license;

  public LicenseEo() {
    this.license = new JsonObject();
  }

  public JsonObject getLicense() {
    return license;
  }

  public void setName(String name){
    this.license = JsonUtil.set(license, "name", name);
  }

  public void setCode(String name){
    this.license = JsonUtil.set(license, "code", name);
  }

  public void setDefinition(String name){
    this.license = JsonUtil.set(license, "definition", name);
  }

  public void setIcon(String name){
    this.license = JsonUtil.set(license, "icon", name);
  }

  public void setUrl(String name){
    this.license = JsonUtil.set(license, "url", name);
  }

}
