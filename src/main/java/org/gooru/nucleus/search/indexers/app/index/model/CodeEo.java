package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

public class CodeEo {

  private JsonObject code;

  public CodeEo() {
    this.code = new JsonObject();
  }

  public JsonObject getCode() {
    return code;
  }

  public void setCode(JsonObject code) {
    this.code = code;
  }

  public String getLabel() {
    return code.getString("label", null);
  }

  public void setLabel(String label) {
    code = JsonUtil.set(code, "label", label);
  }

  public String getCodeId() {
    return code.getString("codeId", null);
  }

  public void setCodeId(String codeId) {
    code = JsonUtil.set(code, "codeId", codeId);
  }

  public Boolean getHasTaxonomyRepresentation() {
    return code.getBoolean("hasTaxonomyRepresentation", null);
  }

  public void setHasTaxonomyRepresentation(Boolean hasTaxonomyRepresentation) {
    code = JsonUtil.set(code, "hasTaxonomyRepresentation", hasTaxonomyRepresentation);
  }

}
