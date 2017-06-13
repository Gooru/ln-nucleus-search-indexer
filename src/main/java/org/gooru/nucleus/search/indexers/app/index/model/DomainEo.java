package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class DomainEo {

  private JsonObject domain = null;

  public DomainEo() {
    this.domain = new JsonObject();
  }

  public JsonObject getDomainJson() {
    return !domain.isEmpty() ? domain : null;
  }
  
  public String getId() {
    return domain.getString("id", null);
  }

  public void setId(String id) {
    domain = JsonUtil.set(domain, "id", id);
  }
  
  public String getTitle() {
    return domain.getString("title", null);
  }

  public void setTitle(String title) {
    domain = JsonUtil.set(domain, "title", title);
  }

}
