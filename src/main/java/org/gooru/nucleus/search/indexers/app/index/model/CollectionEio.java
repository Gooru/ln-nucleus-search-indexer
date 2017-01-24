package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import java.io.Serializable;
import java.util.Date;

public class CollectionEio implements Serializable {

  private static final long serialVersionUID = -7695456210960325029L;
  private JsonObject collection = null;

  public CollectionEio() {
    this.collection = new JsonObject();
  }

  public JsonObject getCollectionJson() {
    return this.collection;
  }

  public String getId() {
    return collection.getString("id", null);
  }

  public void setId(String id) {
    collection = JsonUtil.set(collection, "id", id);
  }

  public String getIndexId() {
    return collection.getString("indexId", null);
  }

  public void setIndexId(String indexId) {
    collection = JsonUtil.set(collection, "indexId", indexId);
  }

  public String getIndexType() {
    return collection.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    collection = JsonUtil.set(collection, "indexType", indexType);
  }

  public String getTitle() {
    return collection.getString("title", null);
  }

  public void setTitle(String title) {
    collection = JsonUtil.set(collection, "title", title);
  }

  public String getIndexUpdatedTime() {
    return collection.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    this.collection = JsonUtil.set(collection, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }

  public String getCreatedAt() {
    return collection.getString("updatedAt", null);
  }

  public void setCreatedAt(String createdAt) {
    this.collection = JsonUtil.set(collection, "createdAt", createdAt);
  }

  public String getUpdatedAt() {
    return collection.getString("updatedAt", null);
  }

  public void setUpdatedAt(String updatedAt) {
    this.collection = JsonUtil.set(collection, "updatedAt", updatedAt);
  }

  public JsonObject getOwner() {
    return collection.getJsonObject("owner", null);
  }

  public void setOwner(JsonObject owner) {
    collection = JsonUtil.set(collection, "owner", owner);
  }

  public JsonObject getCreator() {
    return collection.getJsonObject("creator", null);
  }

  public void setCreator(JsonObject creator) {
    collection = JsonUtil.set(collection, "creator", creator);
  }

  public JsonObject getOriginalCreator() {
    return collection.getJsonObject("originalCreator", null);
  }

  public void setOriginalCreator(JsonObject originalCreator) {
    collection = JsonUtil.set(collection, "originalCreator", originalCreator);
  }

  public String getOriginalCollectionId() {
    return collection.getString("originalCollectionId", null);
  }

  public void setOriginalCollectionId(String originalCollectionId) {
    collection = JsonUtil.set(collection, "originalCollectionId", originalCollectionId);
  }

  public String getParentCollectionId() {
    return collection.getString("parentCollectionId", null);
  }

  public void setParentCollectionId(String parentCollectionId) {
    collection = JsonUtil.set(collection, "parentCollectionId", parentCollectionId);
  }

  public String getPublishDate() {
    return collection.getString("publishDate", null);
  }

  public void setPublishDate(String publishDate) {
    collection = JsonUtil.set(collection, "publishDate", publishDate);
  }

  public String getPublishStatus() {
    return collection.getString("publishStatus", null);
  }

  public void setPublishStatus(String publishStatus) {
    collection = JsonUtil.set(collection, "publishStatus", publishStatus);
  }

  public String getContentFormat() {
    return collection.getString("contentFormat", null);
  }

  public void setContentFormat(String contentFormat) {
    collection = JsonUtil.set(collection, "contentFormat", contentFormat);
  }

  public String getThumbnail() {
    return collection.getString("thumbnail", null);
  }

  public void setThumbnail(String thumbnail) {
    collection = JsonUtil.set(collection, "thumbnail", thumbnail);
  }

  public String getLearningObjective() {
    return collection.getString("learningObjective", null);
  }

  public void setLanguageObjective(String learningObjective) {
    collection = JsonUtil.set(collection, "languageObjective", learningObjective);
  }

  public String getLanguageObjective() {
    return collection.getString("languageObjective", null);
  }

  public void setLearningObjective(String learningObjective) {
    collection = JsonUtil.set(collection, "learningObjective", learningObjective);
  }

  public JsonArray getCollaboratorIds() {
    return collection.getJsonArray("collaboratorIds", null);
  }

  public void setCollaboratorIds(JsonArray collaboratorIds) {
    this.collection = JsonUtil.set(collection, "collaboratorIds", collaboratorIds);
  }

  public JsonObject getMetadata() {
    return collection.getJsonObject("metadata", null);
  }

  public void setMetadata(JsonObject metadata) {
    this.collection = JsonUtil.set(collection, "metadata", metadata);
  }

  public JsonObject getTaxonomy() {
    return collection.getJsonObject("taxonomy", null);
  }

  public void setTaxonomy(JsonObject taxonomy) {
    this.collection = JsonUtil.set(collection, "taxonomy", taxonomy);
  }

  public String getOrientation() {
    return collection.getString("orientation", null);
  }

  public void setOrientation(String orientation) {
    this.collection = JsonUtil.set(collection, "orientation", orientation);
  }

  public String getUrl() {
    return collection.getString("url", null);
  }

  public void setUrl(String url) {
    this.collection = JsonUtil.set(collection, "url", url);
  }

  public Boolean getVisibleOnProfile() {
    return collection.getBoolean("visibleOnProfile", null);
  }

  public void setVisibleOnProfile(Boolean visibleOnProfile) {
    this.collection = JsonUtil.set(collection, "visibleOnProfile", visibleOnProfile);
  }

  public String getGradingType() {
    return collection.getString("gradingType", null);
  }

  public void setGradingType(String gradingType) {
    this.collection = JsonUtil.set(collection, "gradingType", gradingType);
  }

  public JsonObject getStatistics() {
    return collection.getJsonObject("statistics", null);
  }

  public void setStatistics(JsonObject statistics) {
    this.collection = JsonUtil.set(collection, "statistics", statistics);
  }

  public JsonArray getResourceIds() {
    return collection.getJsonArray("resourceIds", null);
  }

  public void setResourceIds(JsonArray resourceIds) {
    this.collection = JsonUtil.set(collection, "resourceIds", resourceIds);
  }

  public JsonArray getResourceTitles() {
    return collection.getJsonArray("resourceTitles", null);
  }

  public void setResourceTitles(JsonArray resourceTitles) {
    this.collection = JsonUtil.set(collection, "resourceTitles", resourceTitles);
  }

  public JsonArray getCollectionContents() {
    return collection.getJsonArray("collectionContents", null);
  }

  public void setCollectionContents(JsonArray collectionContents) {
    this.collection = JsonUtil.set(collection, "collectionContents", collectionContents);
  }

  public String getModifierId() {
    return collection.getString("modifierId", null);
  }

  public void setModifierId(String modifierId) {
    this.collection = JsonUtil.set(collection, "modifierId", modifierId);
  }
  
  public void setLicense(JsonObject license){
    collection = JsonUtil.set(collection, "license", license);
  }

  public JsonObject getLicense(){
    return collection.getJsonObject("license", null);
  }
  
  public JsonObject getCourse() {
    return collection.getJsonObject("course", null);
  }

  public void setCourse(JsonObject course) {
    collection = JsonUtil.set(collection, "course", course);
  }

  public JsonObject getTenant() {
    return collection.getJsonObject("tenant", null);
  }

  public void setTenant(JsonObject tenant) {
    collection = JsonUtil.set(collection, "tenant", tenant);
  }

}
