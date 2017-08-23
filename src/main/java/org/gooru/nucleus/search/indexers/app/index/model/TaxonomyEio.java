package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.Date;
import java.util.stream.Collectors;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TaxonomyEio {

  private JsonObject taxonomy = null;

  public TaxonomyEio() {
    this.taxonomy = new JsonObject();
  }

  public JsonObject getTaxonomyJson() {
    return this.taxonomy;
  }

  public String getId() {
    return taxonomy.getString("id", null);
  }

  public void setId(String id) {
    taxonomy = JsonUtil.set(taxonomy, "id", id);
  }
  
  public String getIndexType() {
    return taxonomy.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    taxonomy = JsonUtil.set(taxonomy, "indexType", indexType);
  }
  
  public String getDisplayCode() {
    return taxonomy.getString("displayCode", null);
  }

  public void setDisplayCode(String displayCode) {
    taxonomy = JsonUtil.set(taxonomy, "displayCode", displayCode);
  }
  
  public String getTitle() {
    return taxonomy.getString("title", null);
  }

  public void setTitle(String title) {
    taxonomy = JsonUtil.set(taxonomy, "title", title);
  }
  
  public String getDescription() {
    return taxonomy.getString("description", null);
  }

  public void setDescription(String description) {
    taxonomy = JsonUtil.set(taxonomy, "description", description);
  }
  
  public String getGutCode() {
    return taxonomy.getString("gutCode", null);
  }

  public void setGutCode(String gutCode) {
    taxonomy = JsonUtil.set(taxonomy, "gutCode", gutCode);
  }
  
  public String getCodeType() {
    return taxonomy.getString("codeType", null);
  }

  public void setCodeType(String codeType) {
    taxonomy = JsonUtil.set(taxonomy, "codeType", codeType);
  }
  
  public String getGrade() {
    return taxonomy.getString("grade", null);
  }

  public void setGrade(String grade) {
    taxonomy = JsonUtil.set(taxonomy, "grade", grade);
  }
  
  public String getCompetency(String key) {
    return taxonomy.getString(key, null);
  }

  public void setCompetency(JsonObject competency) {
    taxonomy = JsonUtil.set(taxonomy, "competency", competency);
  }
  
  public JsonArray getCrosswalkCodes(String key) {
    return taxonomy.getJsonArray(key);
  }

  public void setCrosswalkCodes(JsonArray crosswalkCodes) {
    taxonomy = JsonUtil.set(taxonomy, "crosswalkCodes", crosswalkCodes);
  }

  public String getIndexUpdatedTime() {
    return taxonomy.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    this.taxonomy = JsonUtil.set(taxonomy, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }
  
  public JsonObject getSubject() {
    return taxonomy.getJsonObject("subject", null);
  }

  public void setSubject(JsonObject subject) {
    taxonomy = JsonUtil.set(taxonomy, "subject",subject);
  }

  public JsonObject getCourse() {
    return taxonomy.getJsonObject("course", null);
  }

  public void setCourse(JsonObject course) {
    taxonomy = JsonUtil.set(taxonomy, "course", course);
  }

  public JsonObject getDomain() {
    return taxonomy.getJsonObject("domain", null);
  }

  public void setDomain(JsonObject domain) {
    taxonomy = JsonUtil.set(taxonomy, "domain", domain);
  }
  
  public JsonArray getKeywords() {
    return taxonomy.getJsonArray("keywords", null);
  }

  public void setKeywords(JsonArray keywords) {
    taxonomy = JsonUtil.set(taxonomy, "keywords", new JsonArray(keywords.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getKeywordsSuggestion() {
    return taxonomy.getJsonArray("keywordsSuggestion", null);
  }

  public void setKeywordsSuggestion(JsonArray keywords) {
    taxonomy = JsonUtil.set(taxonomy, "keywordsSuggestion", keywords);
  }

}
