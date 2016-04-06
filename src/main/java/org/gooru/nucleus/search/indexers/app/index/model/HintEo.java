package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

public class HintEo {

  private JsonObject hint;

  public HintEo() {
    this.hint = new JsonObject();
  }

  public JsonObject getHint() {
    return hint;
  }

  public String getHintText() {
    return hint.getString("hintText", null);
  }

  public void setHintText(String hintText) {
    this.hint = JsonUtil.set(hint, "hintText", hintText);
  }

  public Integer getHintCount() {
    return hint.getInteger("hintCount", 0);
  }

  public void setHintCount(Integer hintCount) {
    this.hint = JsonUtil.set(hint, "hintCount", hintCount);
  }

}
