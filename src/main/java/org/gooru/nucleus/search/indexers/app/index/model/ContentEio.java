package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import java.io.Serializable;
import java.util.Date;

public class ContentEio implements Serializable {

  private static final long serialVersionUID = -8299148888835845089L;
  private JsonObject content = null;

  public ContentEio() {
    this.content = new JsonObject();
  }

  public JsonObject getContentJson() {
    return this.content;
  }

  public String getId() {
    return content.getString("id", null);
  }

  public void setId(String id) {
    content = JsonUtil.set(content, "id", id);
  }

	public String getIndexId() {
		return content.getString("indexId", null);
	}

	public void setIndexId(String indexId) {
		content = JsonUtil.set(content, "indexId", indexId);
	}
	
	public String getIndexType() {
		return content.getString("indexType", null);
	}

	public void setIndexType(String indexType) {
		content = JsonUtil.set(content, "indexType", indexType);
	}
	
  public String getUrl() {
    return content.getString("url", null);
  }

  public void setUrl(String url) {
    content = JsonUtil.set(content, "url", url);
  }

  public String getTitle() {
    return content.getString("title", null);
  }

  public void setTitle(String title) {
    content = JsonUtil.set(content, "title", title);
  }

  public String getIndexUpdatedTime() {
    return content.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    content = JsonUtil.set(content, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }

  public String getCreatedAt() {
    return content.getString("createdAt", null);
  }

  public void setCreatedAt(String createdAt) {
    content = JsonUtil.set(content, "createdAt", createdAt);
  }

  public String getUpdatedAt() {
    return content.getString("updatedAt", null);
  }

  public void setUpdatedAt(String updatedAt) {
    content = JsonUtil.set(content, "updatedAt", updatedAt);
  }

  public String getOriginalContentId() {
    return content.getString("originalContentId", null);
  }

  public void setOriginalContentId(String originalContentId) {
    content = JsonUtil.set(content, "originalContentId", originalContentId);
  }

  public String getParentContentId() {
    return content.getString("parentContentId", null);
  }

  public void setParentContentId(String parentContentId) {
    content = JsonUtil.set(content, "parentContentId", parentContentId);
  }

  public JsonObject getOriginalCreator() {
    return content.getJsonObject("originalCreator", null);
  }

  public void setOriginalCreator(JsonObject originalCreator) {
    content = JsonUtil.set(content, "originalCreator", originalCreator);
  }

  public JsonObject getCreator() {
    return content.getJsonObject("creator", null);
  }

  public void setCreator(JsonObject creator) {
    content = JsonUtil.set(content, "creator", creator);
  }

  public String getPublishDate() {
    return content.getString("publishDate", null);
  }

  public void setPublishDate(String publishDate) {
    content = JsonUtil.set(content, "publishDate", publishDate);
  }

  public String getPublishStatus() {
    return content.getString("publishStatus", null);
  }

  public void setPublishStatus(String publishStatus) {
    content = JsonUtil.set(content, "publishStatus", publishStatus);
  }

  public String getShortTitle() {
    return content.getString("shortTitle", null);
  }

  public void setShortTitle(String shortTitle) {
    content = JsonUtil.set(content, "shortTitle", shortTitle);
  }

  public String getNarration() {
    return content.getString("narration", null);
  }

  public void setNarration(String narration) {
    content = JsonUtil.set(content, "narration", narration);
  }

  public String getDescription() {
    return content.getString("description", null);
  }

  public void setDescription(String description) {
    content = JsonUtil.set(content, "description", description);
  }

  public String getContentFormat() {
    return content.getString("contentFormat", null);
  }

  public void setContentFormat(String contentFormat) {
    content = JsonUtil.set(content, "contentFormat", contentFormat);
  }

  public String getContentSubFormat() {
    return content.getString("contentSubFormat", null);
  }

  public void setContentSubFormat(String contentSubFormat) {
    content = JsonUtil.set(content, "contentSubFormat", contentSubFormat);
  }

  public String getContentSubFormatEscaped() {
    return content.getString("contentSubFormatEscaped", null);
  }

  public void setContentSubFormatEscaped(String contentSubFormatEscaped) {
    content = JsonUtil.set(content, "contentSubFormatEscaped", contentSubFormatEscaped);
  }

  public JsonObject getQuestion() {
    return content.getJsonObject("question", null);
  }

  public void setQuestion(JsonObject question) {
    content = JsonUtil.set(content, "question", question);
  }

  public JsonObject getMetadata() {
    return content.getJsonObject("metadata", null);
  }

  public void setMetadata(JsonObject metadata) {
    content = JsonUtil.set(content, "metadata", metadata);
  }

  public JsonObject getTaxonomy() {
    return content.getJsonObject("taxonomy", null);
  }

  public void setTaxonomy(JsonObject taxonomy) {
    content = JsonUtil.set(content, "taxonomy", taxonomy);
  }

  public String getThumbnail() {
    return content.getString("taxonomy", null);
  }

  public void setThumbnail(String thumbnail) {
    content = JsonUtil.set(content, "thumbnail", thumbnail);
  }

  public String getCollectionId() {
    return content.getString("collectionId", null);
  }

  public void setCollectionId(String collectionId) {
    content = JsonUtil.set(content, "collectionId", collectionId);
  }

  public Boolean getIsCopyrightOwner() {
    return content.getBoolean("isCopyrightOwner", null);
  }

  public void setIsCopyrightOwner(Boolean isCopyrightOwner) {
    content = JsonUtil.set(content, "isCopyrightOwner", isCopyrightOwner);
  }

  public JsonObject getCopyrightOwner() {
    return content.getJsonObject("copyrightOwner", null);
  }

  public void setCopyrightOwner(JsonObject copyrightOwner) {
    content = JsonUtil.set(content, "copyrightOwner", copyrightOwner);
  }

  public Boolean getVisibleOnProfile() {
    return content.getBoolean("visibleOnProfile", null);
  }

  public void setVisibleOnProfile(Boolean visibleOnProfile) {
    content = JsonUtil.set(content, "visibleOnProfile", visibleOnProfile);
  }

  public JsonObject getStatistics() {
    return content.getJsonObject("statistics", null);
  }

  public void setStatistics(JsonObject statistics) {
    content = JsonUtil.set(content, "statistics", statistics);
  }

  public JsonArray getCollectionIds() {
    return content.getJsonArray("collectionIds", null);
  }

  public void setCollectionIds(JsonArray collectionIds) {
    content = JsonUtil.set(content, "collectionIds", collectionIds);
  }

  public JsonArray getCollectionTitles() {
    return content.getJsonArray("collectionTitles", null);
  }

  public void setCollectionTitles(JsonArray collectionTitles) {
    content = JsonUtil.set(content, "collectionTitles", collectionTitles);
  }

  public String getGrade() {
    return content.getString("grade", null);
  }

  public void setGrade(String grade) {
    content = JsonUtil.set(content, "grade", grade);
  }

}
