package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.IndexFields;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CourseEio extends JsonObject {


  public String getId() {
    return this.getString(IndexFields.ID);
  }

  public void setId(String id) {
    this.put(IndexFields.ID, id);
  }
  
  public String getIndexId() {
    return this.getString(IndexFields.INDEX_ID, null);
  }

  public void setIndexId(String indexId) {
    this.put(IndexFields.INDEX_ID, indexId);
  }

  public String getIndexType() {
    return this.getString(IndexFields.INDEX_TYPE, null);
  }

  public void setIndexType(String indexType) {
    this.put(IndexFields.INDEX_TYPE, indexType);
  }

  public String getTitle() {
    return this.getString(IndexFields.TITLE);
  }

  public void setTitle(String title) {
    this.put(IndexFields.TITLE, title);
  }

  public String getOriginalCourseId() {
    return this.getString(IndexFields.ORIGINAL_COURSE_ID);
  }

  public void setOriginalCourseId(String originalCourseId) {
    this.put(IndexFields.ORIGINAL_COURSE_ID, originalCourseId);
  }

  public String getParentCourseId() {
    return this.getString(IndexFields.PARENT_COURSE_ID);
  }

  public void setParentCourseId(String parentCourseId) {
    this.put(IndexFields.PARENT_COURSE_ID, parentCourseId);
  }

  public String getCreatedAt() {
    return this.getString(IndexFields.CREATED_AT);
  }

  public void setCreatedAt(String createdAt) {
    this.put(IndexFields.CREATED_AT, createdAt);
  }

  public String getUpdatedAt() {
    return this.getString(IndexFields.UPDATED_AT);
  }

  public void setUpdatedAt(String updatedAt) {
    this.put(IndexFields.UPDATED_AT, updatedAt);
  }

  public String getIndexUpdatedTime() {
    return this.getString(IndexFields.INDEX_UPDATED_TIME);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    this.put(IndexFields.INDEX_UPDATED_TIME, indexUpdatedTime.toInstant());
  }

  public JsonObject getOwner() {
    return this.getJsonObject(IndexFields.OWNER);
  }

  public void setOwner(JsonObject owner) {
    this.put(IndexFields.OWNER, owner);
  }

  public JsonObject getCreator() {
    return this.getJsonObject(IndexFields.CREATOR);
  }

  public void setCreator(JsonObject creator) {
    this.put(IndexFields.CREATOR, creator);
  }

  public JsonObject getOriginalCreator() {
    return this.getJsonObject(IndexFields.ORIGINAL_CREATOR);
  }

  public void setOriginalCreator(JsonObject originalCreator) {
    this.put(IndexFields.ORIGINAL_CREATOR, originalCreator);
  }

  public CourseStatisticsEo getStatistics() {
    return (CourseStatisticsEo) this.getJsonObject(IndexFields.STATISTICS);
  }

  public void setStatistics(CourseStatisticsEo statistics) {
    this.put(IndexFields.STATISTICS, statistics);
  }

  public Boolean getVisibleOnProfile() {
    return this.getBoolean(IndexFields.VISIBLE_ON_PROFILE);
  }

  public void setVisibleOnProfile(Boolean visibleOnProfile) {
    this.put(IndexFields.VISIBLE_ON_PROFILE, visibleOnProfile);
  }

  public String getModifierId() {
    return this.getString(IndexFields.MODIFIER_ID);
  }

  public void setModifierId(String modifierId) {
    this.put(IndexFields.MODIFIER_ID, modifierId);
  }

  public String getSubjectBucket() {
    return this.getString(IndexFields.SUBJECT_BUCKET);
  }

  public void setSubjectBucket(String subjectBucket) {
    this.put(IndexFields.SUBJECT_BUCKET, subjectBucket);
  }

  public String getDescription() {
    return this.getString(IndexFields.DESCRIPTION);
  }

  public void setDescription(String description) {
    this.put(IndexFields.DESCRIPTION, description);
  }

  public int getSubjectSequence() {
    return this.getInteger(IndexFields.SUBJECT_SEQUENCE);
  }

  public void setSubjectSequence(int subjectSequence) {
    this.put(IndexFields.SUBJECT_SEQUENCE, subjectSequence);
  }

  public int getSequenceId() {
    return this.getInteger(IndexFields.SEQUENCE);
  }

  public void setSequenceId(int sequenceId) {
    this.put(IndexFields.SEQUENCE, sequenceId);
  }

  public String getThumbnail() {
    return this.getString(IndexFields.THUMBNAIL);
  }

  public void setThumbnail(String thumbnail) {
    this.put(IndexFields.THUMBNAIL, thumbnail);
  }

  public String getPublishStatus() {
    return this.getString(IndexFields.PUBLISH_STATUS);
  }

  public void setPublishStatus(String publishStatus) {
    this.put(IndexFields.PUBLISH_STATUS, publishStatus);
  }

  public JsonObject getTaxonomy() {
    return this.getJsonObject(IndexFields.TAXONOMY);
  }

  public void setTaxonomy(JsonObject taxonomy) {
    this.put(IndexFields.TAXONOMY, taxonomy);
  }

  public JsonObject getLicense() {
    return this.getJsonObject(IndexFields.LICENSE);
  }

  public void setLicense(JsonObject license) {
    this.put(IndexFields.LICENSE, license);
  }

  public String getPublishDate() {
    return this.getString(IndexFields.PUBLISH_DATE);
  }

  public void setPublishDate(String publishDate) {
    this.put(IndexFields.PUBLISH_DATE, publishDate);
  }
  
  public JsonObject getResourceInfo() {
    return this.getJsonObject(IndexFields.RESOURCE_INFO, null);
  }

  public void setResourceInfo(JsonObject resourceInfo) {
    this.put(IndexFields.RESOURCE_INFO, resourceInfo);
  }
  
  public JsonObject getTenant() {
    return this.getJsonObject(IndexFields.TENANT, null);
  }

  public void setTenant(JsonObject tenant) {
    this.put(IndexFields.TENANT, tenant);
  }
  
  public JsonArray getUnitIds() {
    return this.getJsonArray(IndexFields.UNIT_IDS, null);
  }

  public void setUnitIds(JsonArray unitIds) {
    this.put(IndexFields.UNIT_IDS, unitIds);
  }
  
  public JsonArray getUnitTitles() {
    return this.getJsonArray(IndexFields.UNIT_TITLES, null);
  }

  public void setUnitTitles(JsonArray unitTitles) {
    this.put(IndexFields.UNIT_TITLES, unitTitles);
  }
  
  public JsonArray getLessonIds() {
    return this.getJsonArray(IndexFields.LESSON_IDS, null);
  }

  public void setLessonIds(JsonArray lessonIds) {
    this.put(IndexFields.LESSON_IDS, lessonIds);
  }
  
  public JsonArray getLessonTitles() {
    return this.getJsonArray(IndexFields.LESSON_TITLES, null);
  }

  public void setLessonTitles(JsonArray lessonTitles) {
    this.put(IndexFields.LESSON_TITLES, lessonTitles);
  }
  
  public JsonArray getCollectionIds() {
    return this.getJsonArray(IndexFields.COLLECTION_IDS, null);
  }

  public void setCollectionIds(JsonArray collectionIds) {
    this.put(IndexFields.COLLECTION_IDS, collectionIds);
  }

  public JsonArray getCollectionTitles() {
    return this.getJsonArray(IndexFields.COLLECTION_TITLES, null);
  }

  public void setCollectionTitles(JsonArray collectionTitles) {
    this.put(IndexFields.COLLECTION_TITLES, collectionTitles);
  }
  
  public JsonArray getCollections() {
    return this.getJsonArray(IndexFields.COLLECTIONS, null);
  }

  public void setCollections(JsonArray collections) {
    this.put(IndexFields.COLLECTIONS, collections);
  }
}
