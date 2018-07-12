package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class RubricEio {

	private JsonObject rubric = null;

	public RubricEio() {
		this.rubric = new JsonObject();
	}

	public JsonObject getRubricJson() {
		return this.rubric;
	}

	public String getId() {
		return rubric.getString("id", null);
	}

	public void setId(String id) {
		rubric = JsonUtil.set(rubric, "id", id);
	}

	public String getIndexId() {
		return rubric.getString("indexId", null);
	}

	public void setIndexId(String indexId) {
		rubric = JsonUtil.set(rubric, "indexId", indexId);
	}

	public String getIndexType() {
		return rubric.getString("indexType", null);
	}

	public void setIndexType(String indexType) {
		rubric = JsonUtil.set(rubric, "indexType", indexType);
	}

	public String getTitle() {
		return rubric.getString("title", null);
	}

	public void setTitle(String title) {
		rubric = JsonUtil.set(rubric, "title", title);
	}

	public String getUrl() {
		return rubric.getString("url", null);
	}

	public void setUrl(String url) {
		this.rubric = JsonUtil.set(rubric, "url", url);
	}

	public String getDescription() {
		return rubric.getString("description", null);
	}

	public void setDescription(String description) {
		rubric = JsonUtil.set(rubric, "description", description);
	}

	public String getFeedbackGuidance() {
		return rubric.getString("feedbackGuidance", null);
	}

	public void setFeedbackGuidance(String feedbackGuidance) {
		rubric = JsonUtil.set(rubric, "feedbackGuidance", feedbackGuidance);
	}
	
	public JsonObject getCategories() {
		return rubric.getJsonObject("categories", null);
	}

	public void setCategories(JsonObject categories) {
		rubric = JsonUtil.set(rubric, "categories", categories);
	}
	
	public String getIndexUpdatedTime() {
		return rubric.getString("indexUpdatedTime", null);
	}

	public void setIndexUpdatedTime(Date indexUpdatedTime) {
		this.rubric = JsonUtil.set(rubric, "indexUpdatedTime", indexUpdatedTime.toInstant());
	}

	public String getCreatedAt() {
		return rubric.getString("updatedAt", null);
	}

	public void setCreatedAt(String createdAt) {
		this.rubric = JsonUtil.set(rubric, "createdAt", createdAt);
	}

	public String getUpdatedAt() {
		return rubric.getString("updatedAt", null);
	}

	public void setUpdatedAt(String updatedAt) {
		this.rubric = JsonUtil.set(rubric, "updatedAt", updatedAt);
	}

	public JsonObject getCreator() {
		return rubric.getJsonObject("creator", null);
	}

	public void setCreator(JsonObject creator) {
		rubric = JsonUtil.set(rubric, "creator", creator);
	}

	public JsonObject getOriginalCreator() {
		return rubric.getJsonObject("originalCreator", null);
	}

	public void setOriginalCreator(JsonObject originalCreator) {
		rubric = JsonUtil.set(rubric, "originalCreator", originalCreator);
	}

	public String getModifierId() {
		return rubric.getString("modifierId", null);
	}

	public void setModifierId(String modifierId) {
		this.rubric = JsonUtil.set(rubric, "modifierId", modifierId);
	}

	public String getOriginalRubricId() {
		return rubric.getString("originalRubricId", null);
	}

	public void setOriginalRubricId(String originalRubricId) {
		rubric = JsonUtil.set(rubric, "originalRubricId", originalRubricId);
	}

	public String getParentRubricId() {
		return rubric.getString("parentRubricId", null);
	}

	public void setParentRubricId(String parentRubricId) {
		rubric = JsonUtil.set(rubric, "parentRubricId", parentRubricId);
	}

	public String getPublishDate() {
		return rubric.getString("publishDate", null);
	}

	public void setPublishDate(String publishDate) {
		rubric = JsonUtil.set(rubric, "publishDate", publishDate);
	}

	public String getPublishStatus() {
		return rubric.getString("publishStatus", null);
	}

	public void setPublishStatus(String publishStatus) {
		rubric = JsonUtil.set(rubric, "publishStatus", publishStatus);
	}

	public String getContentFormat() {
		return rubric.getString("contentFormat", null);
	}

	public void setContentFormat(String contentFormat) {
		rubric = JsonUtil.set(rubric, "contentFormat", contentFormat);
	}

	public JsonObject getMetadata() {
		return rubric.getJsonObject("metadata", null);
	}

	public void setMetadata(JsonObject metadata) {
		rubric = JsonUtil.set(rubric, "metadata", metadata);
	}

	public JsonObject getTaxonomy() {
		return rubric.getJsonObject("taxonomy", null);
	}

	public void setTaxonomy(JsonObject taxonomy) {
		rubric = JsonUtil.set(rubric, "taxonomy", taxonomy);
	}

	public String getThumbnail() {
		return rubric.getString("thumbnail", null);
	}

	public void setThumbnail(String thumbnail) {
		rubric = JsonUtil.set(rubric, "thumbnail", thumbnail);
	}

	public JsonObject getTenant() {
		return rubric.getJsonObject("tenant", null);
	}

	public void setTenant(JsonObject tenant) {
		rubric = JsonUtil.set(rubric, "tenant", tenant);
	}

	public String getCollectionId() {
		return rubric.getString("collectionId", null);
	}

	public void setCollectionId(String collectionId) {
		rubric = JsonUtil.set(rubric, "collectionId", collectionId);
	}

	public JsonObject getCourse() {
		return rubric.getJsonObject("course", null);
	}

	public void setCourse(JsonObject course) {
		rubric = JsonUtil.set(rubric, "course", course);
	}

	public JsonObject getUnit() {
		return rubric.getJsonObject("unit", null);
	}

	public void setUnit(JsonObject unit) {
		rubric = JsonUtil.set(rubric, "unit", unit);
	}

	public JsonObject getLesson() {
		return rubric.getJsonObject("lesson", null);
	}

	public void setLesson(JsonObject lesson) {
		rubric = JsonUtil.set(rubric, "lesson", lesson);
	}

	public JsonObject getCollection() {
		return rubric.getJsonObject("collection", null);
	}

	public void setCollection(JsonObject collection) {
		rubric = JsonUtil.set(rubric, "collection", collection);
	}
	
	public JsonObject getContent() {
		return rubric.getJsonObject("content", null);
	}

	public void setContent(JsonObject content) {
		rubric = JsonUtil.set(rubric, "content", content);
	}

	public JsonObject getStatistics() {
		return rubric.getJsonObject("statistics", null);
	}

	public void setStatistics(JsonObject statistics) {
		this.rubric = JsonUtil.set(rubric, "statistics", statistics);
	}
	
	public JsonObject getLibrary() {
	    return rubric.getJsonObject("library", null);
	}

	public void setLibrary(JsonObject library) {
	  this.rubric = JsonUtil.set(rubric, "library", library);
	}
}
