package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class CollectionEo {

  private JsonObject collection = null;

  public CollectionEo() {
    this.collection = new JsonObject();
  }

  public JsonObject getCollectionJson() {
    return !collection.isEmpty() ? collection : null;
  }

  public String getId() {
    return collection.getString("id", null);
  }

  public void setId(String id) {
    collection = JsonUtil.set(collection, "id", id);
  }

  public String getTitle() {
    return collection.getString("title", null);
  }

  public void setTitle(String title) {
    collection = JsonUtil.set(collection, "title", title);
  }

  public String getDescription() {
    return collection.getString("description", null);
  }

  public void setDescription(String description) {
    collection = JsonUtil.set(collection, "description", description);
  }
  
  public String getThumbnail() {
    return collection.getString("thumbnail", null);
  }

  public void setThumbnail(String thumbnail) {
    collection = JsonUtil.set(collection, "thumbnail", thumbnail);
  }

  public String getUrl() {
    return collection.getString("url", null);
  }

  public void setUrl(String url) {
    collection = JsonUtil.set(collection, "url", url);
  }

  public JsonObject getOwner() {
    return collection.getJsonObject("owner", null);
  }

  public String getContentSubFormat() {
    return collection.getString("contentSubFormat", null);
  }

  public void setContentSubFormat(String contentSubFormat) {
    collection = JsonUtil.set(collection, "contentSubFormat", contentSubFormat);
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

  public Double getEfficacy() {
    return collection.getDouble("efficacy", 0.5);
  }

  public void setEfficacy(Double efficacy) {
    if (efficacy == null) {
      efficacy = 0.5;
    }
    this.collection = JsonUtil.set(collection, "efficacy", efficacy);
  }

  public Double getEngagement() {
    return collection.getDouble("engagement", 0.5);
  }

  public void setEngagement(Double engagement) {
    if (engagement == null) {
      engagement = 0.5;
    }
    this.collection = JsonUtil.set(collection, "engagement", engagement);
  }

  public Double getRelevance() {
    return collection.getDouble("relevance", 0.5);
  }

  public void setRelevance(Double relevance) {
    if (relevance == null) {
      relevance = 0.5;
    }
    this.collection = JsonUtil.set(collection, "relevance", relevance);
  }
  
  public Boolean isCurated() {
    return collection.getBoolean("isCurated", false);
  }

  public void setCurated(Boolean isCurated) {
    collection = JsonUtil.set(collection, "isCurated", isCurated);
  }
  
  public JsonObject getPrimaryLanguage() {
    return collection.getJsonObject("primaryLanguage", null);
  }

  public void setPrimaryLanguage(JsonObject primaryLanguage) {
    this.collection = JsonUtil.set(collection, "primaryLanguage", primaryLanguage);
  }

}
