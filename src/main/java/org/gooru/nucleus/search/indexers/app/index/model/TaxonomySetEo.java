package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.stream.Collectors;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TaxonomySetEo {

  private JsonObject taxonomySet = null;

  public TaxonomySetEo() {
    this.taxonomySet = new JsonObject();
  }

  public JsonObject getTaxonomyJson() {
    return this.taxonomySet;
  }

  public JsonArray getSubject() {
    return taxonomySet.getJsonArray("subject", null);
  }

  public void setSubject(JsonArray subject) {
    taxonomySet = JsonUtil.set(taxonomySet, "subject", new JsonArray(subject.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getCourse() {
    return taxonomySet.getJsonArray("course", null);
  }

  public void setCourse(JsonArray course) {
    taxonomySet = JsonUtil.set(taxonomySet, "course", new JsonArray(course.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getDomain() {
    return taxonomySet.getJsonArray("domain", null);
  }

  public void setDomain(JsonArray domain) {
    taxonomySet = JsonUtil.set(taxonomySet, "domain", new JsonArray(domain.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getTaxonomy() {
    return taxonomySet.getJsonArray("taxonomy", null);
  }

  public void setTaxonomy(JsonArray taxonomy) {
    taxonomySet = JsonUtil.set(taxonomySet, "taxonomy", taxonomy);
  }
  
  public JsonObject getCurriculum() {
    return taxonomySet.getJsonObject("curriculum", null);
  }

  public void setCurriculum(JsonObject curriculum) {
    taxonomySet = JsonUtil.set(taxonomySet, "curriculum", curriculum);
  }

}
