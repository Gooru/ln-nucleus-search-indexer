package org.gooru.nucleus.search.indexers.app.index.model;

import java.io.Serializable;
import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UnitEio implements Serializable {

  private static final long serialVersionUID = -7695456210960325030L;
  private JsonObject unit = null;

  public UnitEio() {
    this.unit = new JsonObject();
  }

  public JsonObject getUnitJson() {
    return this.unit;
  }

  public String getId() {
    return unit.getString("id", null);
  }

  public void setId(String id) {
    unit = JsonUtil.set(unit, "id", id);
  }
  
  public String getIndexId() {
    return unit.getString("indexId", null);
  }

  public void setIndexId(String indexId) {
    unit = JsonUtil.set(unit, "indexId", indexId);
  }

  public String getIndexType() {
    return unit.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    unit = JsonUtil.set(unit, "indexType", indexType);
  }

  public String getTitle() {
    return unit.getString("title", null);
  }

  public void setTitle(String title) {
    unit = JsonUtil.set(unit, "title", title);
  }

  public String getIndexUpdatedTime() {
    return unit.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    unit = JsonUtil.set(unit, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }

  public String getCreatedAt() {
    return unit.getString("createdAt", null);
  }

  public void setCreatedAt(String createdAt) {
    unit = JsonUtil.set(unit, "createdAt", createdAt);
  }

  public String getUpdatedAt() {
    return unit.getString("updatedAt", null);
  }

  public void setUpdatedAt(String updatedAt) {
    unit = JsonUtil.set(unit, "updatedAt", updatedAt);
  }

  public JsonObject getOriginalCreator() {
    return unit.getJsonObject("originalCreator", null);
  }

  public void setOriginalCreator(JsonObject originalCreator) {
    unit = JsonUtil.set(unit, "originalCreator", originalCreator);
  }
  
  public JsonObject getCreator() {
    return unit.getJsonObject("creator", null);
  }

  public void setCreator(JsonObject creator) {
    unit = JsonUtil.set(unit, "creator", creator);
  }
  
  public JsonObject getOwner() {
    return unit.getJsonObject("owner", null);
  }

  public void setOwner(JsonObject owner) {
    unit = JsonUtil.set(unit, "owner", owner);
  }
  
  public String getModifierId() {
    return unit.getString("modifierId", null);
  }

  public void setModifierId(String modifierId) {
    this.unit = JsonUtil.set(unit, "modifierId", modifierId);
  }
  
  public String getContentFormat() {
    return unit.getString("contentFormat", null);
  }

  public void setContentFormat(String contentFormat) {
    unit = JsonUtil.set(unit, "contentFormat", contentFormat);
  }
 
  public String getOriginalUnitId() {
    return unit.getString("originalUnitId", null);
  }

  public void setOriginalUnitId(String originalUnitId) {
    unit = JsonUtil.set(unit, "originalUnitId", originalUnitId);
  }

  public String getParentUnitId() {
    return unit.getString("parentUnitId", null);
  }

  public void setParentUnitId(String parentUnitId) {
    unit = JsonUtil.set(unit, "parentUnitId", parentUnitId);
  }
  
  public JsonObject getTaxonomy() {
    return unit.getJsonObject("taxonomy", null);
  }

  public void setTaxonomy(JsonObject taxonomy) {
    unit = JsonUtil.set(unit, "taxonomy", taxonomy);
  }
  
  public JsonObject getStatistics() {
    return unit.getJsonObject("statistics", null);
  }

  public void setStatistics(JsonObject statistics) {
    unit = JsonUtil.set(unit, "statistics", statistics);
  }
  
  public JsonArray getLessonIds() {
    return unit.getJsonArray("lessonIds", null);
  }

  public void setLessonIds(JsonArray lessonIds) {
    unit = JsonUtil.set(unit, "lessonIds", lessonIds);
  }
  
  public JsonArray getLessonTitles() {
    return unit.getJsonArray("lessonTitles", null);
  }

  public void setLessonTitles(JsonArray lessonTitles) {
    unit = JsonUtil.set(unit, "lessonTitles", lessonTitles);
  }
  
  public JsonObject getTenant() {
    return unit.getJsonObject("tenant", null);
  }

  public void setTenant(JsonObject tenant) {
    unit = JsonUtil.set(unit, "tenant", tenant);
  }
  
  public JsonArray getCollaboratorIds() {
    return unit.getJsonArray("collaboratorIds", null);
  }

  public void setCollaboratorIds(JsonArray collaboratorIds) {
    this.unit = JsonUtil.set(unit, "collaboratorIds", collaboratorIds);
  }

  public JsonArray getCollectionIds() {
    return unit.getJsonArray("collectionIds", null);
  }

  public void setCollectionIds(JsonArray collectionIds) {
    unit = JsonUtil.set(unit, "collectionIds", collectionIds);
  }

  public JsonArray getCollectionTitles() {
    return unit.getJsonArray("collectionTitles", null);
  }

  public void setCollectionTitles(JsonArray collectionTitles) {
    unit = JsonUtil.set(unit, "collectionTitles", collectionTitles);
  }
  
  public JsonArray getCollections() {
    return unit.getJsonArray("collections", null);
  }

  public void setCollections(JsonArray collections) {
    this.unit = JsonUtil.set(unit, "collections", collections);
  }
  
  public JsonObject getCourse() {
    return unit.getJsonObject("course", null);
  }

  public void setCourse(JsonObject course) {
    unit = JsonUtil.set(unit, "course", course);
  }
  
  public String getPublishStatus() {
    return unit.getString(IndexFields.PUBLISH_STATUS);
  }

  public void setPublishStatus(String publishStatus) {
    unit = JsonUtil.set(unit, IndexFields.PUBLISH_STATUS, publishStatus);
  }
  
  public JsonObject getLibrary() {
    return unit.getJsonObject("library", null);
  }

  public void setLibrary(JsonObject library) {
    this.unit = JsonUtil.set(unit, "library", library);
  }
  
  public JsonObject getPrimaryLanguage() {
    return unit.getJsonObject("primaryLanguage", null);
  }

  public void setPrimaryLanguage(JsonObject primaryLanguage) {
    this.unit = JsonUtil.set(unit, "primaryLanguage", primaryLanguage);
  }
}
