package org.gooru.nucleus.search.indexers.app.index.model;

import java.io.Serializable;
import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class LessonEio implements Serializable {

  private static final long serialVersionUID = -7695456210960325031L;
  private JsonObject lesson = null;

  public LessonEio() {
    this.lesson = new JsonObject();
  }

  public JsonObject getLessonJson() {
    return this.lesson;
  }

  public String getId() {
    return lesson.getString("id", null);
  }

  public void setId(String id) {
    lesson = JsonUtil.set(lesson, "id", id);
  }
  
  public String getIndexId() {
    return lesson.getString("indexId", null);
  }

  public void setIndexId(String indexId) {
    lesson = JsonUtil.set(lesson, "indexId", indexId);
  }

  public String getIndexType() {
    return lesson.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    lesson = JsonUtil.set(lesson, "indexType", indexType);
  }

  public String getTitle() {
    return lesson.getString("title", null);
  }

  public void setTitle(String title) {
    lesson = JsonUtil.set(lesson, "title", title);
  }

  public String getIndexUpdatedTime() {
    return lesson.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    lesson = JsonUtil.set(lesson, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }

  public String getCreatedAt() {
    return lesson.getString("createdAt", null);
  }

  public void setCreatedAt(String createdAt) {
    lesson = JsonUtil.set(lesson, "createdAt", createdAt);
  }

  public String getUpdatedAt() {
    return lesson.getString("updatedAt", null);
  }

  public void setUpdatedAt(String updatedAt) {
    lesson = JsonUtil.set(lesson, "updatedAt", updatedAt);
  }
  
  public JsonObject getCreator() {
    return lesson.getJsonObject("creator", null);
  }

  public void setCreator(JsonObject creator) {
    lesson = JsonUtil.set(lesson, "creator", creator);
  }
  
  public JsonObject getOwner() {
    return lesson.getJsonObject("owner", null);
  }

  public void setOwner(JsonObject owner) {
    lesson = JsonUtil.set(lesson, "owner", owner);
  }
  
  public String getContentFormat() {
    return lesson.getString("contentFormat", null);
  }

  public void setContentFormat(String contentFormat) {
    lesson = JsonUtil.set(lesson, "contentFormat", contentFormat);
  }
  
  public JsonObject getTaxonomy() {
    return lesson.getJsonObject("taxonomy", null);
  }

  public void setTaxonomy(JsonObject taxonomy) {
    lesson = JsonUtil.set(lesson, "taxonomy", taxonomy);
  }
  
  public JsonObject getStatistics() {
    return lesson.getJsonObject("statistics", null);
  }

  public void setStatistics(JsonObject statistics) {
    lesson = JsonUtil.set(lesson, "statistics", statistics);
  }
  
  public JsonObject getTenant() {
    return lesson.getJsonObject("tenant", null);
  }

  public void setTenant(JsonObject tenant) {
    lesson = JsonUtil.set(lesson, "tenant", tenant);
  }
  
  public JsonArray getCollaboratorIds() {
    return lesson.getJsonArray("collaboratorIds", null);
  }

  public void setCollaboratorIds(JsonArray collaboratorIds) {
    this.lesson = JsonUtil.set(lesson, "collaboratorIds", collaboratorIds);
  }

  public String getModifierId() {
    return lesson.getString("modifierId", null);
  }

  public void setModifierId(String modifierId) {
    this.lesson = JsonUtil.set(lesson, "modifierId", modifierId);
  }
  
  public JsonArray getCollectionIds() {
    return lesson.getJsonArray("collectionIds", null);
  }

  public void setCollectionIds(JsonArray collectionIds) {
    lesson = JsonUtil.set(lesson, "collectionIds", collectionIds);
  }

  public JsonArray getCollectionTitles() {
    return lesson.getJsonArray("collectionTitles", null);
  }

  public void setCollectionTitles(JsonArray collectionTitles) {
    lesson = JsonUtil.set(lesson, "collectionTitles", collectionTitles);
  }
  
  public JsonArray getCollections() {
    return lesson.getJsonArray("collections", null);
  }

  public void setCollections(JsonArray collections) {
    this.lesson = JsonUtil.set(lesson, "collections", collections);
  }
  
  public JsonObject getCourse() {
    return lesson.getJsonObject("course", null);
  }

  public void setCourse(JsonObject course) {
    lesson = JsonUtil.set(lesson, "course", course);
  }
  
  public JsonObject getUnit() {
    return lesson.getJsonObject("unit", null);
  }

  public void setUnit(JsonObject unit) {
    lesson = JsonUtil.set(lesson, "unit", unit);
  }
  
  public String getPublishStatus() {
    return lesson.getString(IndexFields.PUBLISH_STATUS);
  }

  public void setPublishStatus(String publishStatus) {
    lesson = JsonUtil.set(lesson, IndexFields.PUBLISH_STATUS, publishStatus);
  }
}
