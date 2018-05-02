package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.Date;
import java.util.stream.Collectors;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class GutEio {

  private JsonObject gut = null;

  public GutEio() {
    this.gut = new JsonObject();
  }

  public JsonObject getGutJson() {
    return this.gut;
  }

  public String getId() {
    return gut.getString("id", null);
  }

  public void setId(String id) {
    gut = JsonUtil.set(gut, "id", id);
  }
  
  public String getIndexType() {
    return gut.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    gut = JsonUtil.set(gut, "indexType", indexType);
  }

  public String getIndexUpdatedTime() {
    return gut.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    this.gut = JsonUtil.set(gut, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }
  
  public String getDisplayCode() {
    return gut.getString("code", null);
  }

  public void setDisplayCode(String displayCode) {
    gut = JsonUtil.set(gut, "code", displayCode);
  }

  public String getTitle() {
    return gut.getString("title", null);
  }

  public void setTitle(String title) {
    gut = JsonUtil.set(gut, "title", title);
  }

  public String getCodeType() {
    return gut.getString("codeType", null);
  }

  public void setCodeType(String codeType) {
    gut = JsonUtil.set(gut, "codeType", codeType);
  }
  
  public JsonObject getSubject() {
    return gut.getJsonObject("subject", null);
  }

  public void setSubject(JsonObject subject) {
    gut = JsonUtil.set(gut, "subject",subject);
  }

  public JsonObject getCourse() {
    return gut.getJsonObject("course", null);
  }

  public void setCourse(JsonObject course) {
    gut = JsonUtil.set(gut, "course", course);
  }

  public JsonObject getDomain() {
    return gut.getJsonObject("domain", null);
  }

  public void setDomain(JsonObject domain) {
    gut = JsonUtil.set(gut, "domain", domain);
  }
  
  public String getCompetency(String key) {
    return gut.getString(key, null);
  }

  public void setCompetency(JsonObject competency) {
    gut = JsonUtil.set(gut, "competency", competency);
  }
  
  public String getSubjectLabel() {
    return gut.getString("subjectLabel", null);
  }

  public void setSubjectLabel(String subjectLabel) {
    gut = JsonUtil.set(gut, "subjectLabel", subjectLabel);
  }

  public String getCourseLabel() {
    return gut.getString("courseLabel", null);
  }

  public void setCourseLabel(String courseLabel) {
    gut = JsonUtil.set(gut, "courseLabel", courseLabel);
  }

  public String getDomainLabel() {
    return gut.getString("domainLabel", null);
  }

  public void setDomainLabel(String domainLabel) {
    gut = JsonUtil.set(gut, "domainLabel", domainLabel);
  }
  
  public JsonArray getPrerequisites() {
    return gut.getJsonArray("prerequisites", null);
  }

  public void setPrerequisites(JsonArray prerequisites) {
    gut = JsonUtil.set(gut, "prerequisites", new JsonArray(prerequisites.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getSignatureResources() {
    return gut.getJsonArray("signatureResources", null);
  }

  public void setSignatureResources(JsonArray signatureResources) {
    gut = JsonUtil.set(gut, "signatureResources", new JsonArray(signatureResources.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getSignatureCollections() {
    return gut.getJsonArray("signatureCollections", null);
  }

  public void setSignatureCollections(JsonArray signatureCollections) {
    gut = JsonUtil.set(gut, "signatureCollections", new JsonArray(signatureCollections.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getSignatureAssessments() {
    return gut.getJsonArray("signatureAssessments", null);
  }

  public void setSignatureAssessments(JsonArray signatureAssessments) {
    gut = JsonUtil.set(gut, "signatureAssessments", new JsonArray(signatureAssessments.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getCrosswalkCodes(String key) {
    return gut.getJsonArray(key);
  }

  public void setCrosswalkCodes(JsonArray crosswalkCodes) {
    gut = JsonUtil.set(gut, "crosswalkCodes", crosswalkCodes);
  }
  
  public JsonArray getKeywords() {
    return gut.getJsonArray("keywords", null);
  }

  public void setKeywords(JsonArray keywords) {
    gut = JsonUtil.set(gut, "keywords", new JsonArray(keywords.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getKeywordsSuggestion() {
    return gut.getJsonArray("keywordsSuggestion", null);
  }

  public void setKeywordsSuggestion(JsonArray keywords) {
    gut = JsonUtil.set(gut, "keywordsSuggestion", keywords);
  }
  
}
