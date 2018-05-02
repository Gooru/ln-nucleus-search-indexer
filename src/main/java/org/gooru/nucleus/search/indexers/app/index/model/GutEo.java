package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.stream.Collectors;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class GutEo extends JsonObject {

  private JsonObject gut = null;

  public GutEo() {
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

  public String getSubject() {
    return gut.getString("subject", null);
  }

  public void setSubject(String subject) {
    gut = JsonUtil.set(gut, "subject", subject);
  }

  public String getCourse() {
    return gut.getString("course", null);
  }

  public void setCourse(String course) {
    gut = JsonUtil.set(gut, "course", course);
  }

  public String getDomain() {
    return gut.getString("domain", null);
  }

  public void setDomain(String domain) {
    gut = JsonUtil.set(gut, "domain", domain);
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
  
}
