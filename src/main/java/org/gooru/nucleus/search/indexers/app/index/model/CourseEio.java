package org.gooru.nucleus.search.indexers.app.index.model;

import java.io.Serializable;
import java.util.Date;

import io.vertx.core.json.JsonObject;

public class CourseEio extends JsonObject implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String id;

  private String title;
  
  private String originalCourseId;
  
  private String parentCourseId;
  
  private String createdAt;
  
  private String updatedAt;
  
  private Date indexUpdatedTime;
  
  private JsonObject owner;
  
  private JsonObject creator;
  
  private JsonObject originalCreator;
  
  private CourseStatisticsEo statistics;
  
  private JsonObject license;
  
  private Boolean visibleOnProfile;
  
  private String modifierId;
  
  private String subjectBucket;
  
  private String description;
  
  private int subjectSequence;
  
  private int sequenceId;
  
  private String thumbnail;
  
  private String publishStatus;
  
  private String publishDate;
  
  private int isFeatured;
  
  private JsonObject taxonomy;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getOriginalCourseId() {
    return originalCourseId;
  }

  public void setOriginalCourseId(String originalCourseId) {
    this.originalCourseId = originalCourseId;
  }

  public String getParentCourseId() {
    return parentCourseId;
  }

  public void setParentCourseId(String parentCourseId) {
    this.parentCourseId = parentCourseId;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Date getIndexUpdatedTime() {
    return indexUpdatedTime;
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    this.indexUpdatedTime = indexUpdatedTime;
  }

  public JsonObject getOwner() {
    return owner;
  }

  public void setOwner(JsonObject owner) {
    this.owner = owner;
  }

  public JsonObject getCreator() {
    return creator;
  }

  public void setCreator(JsonObject creator) {
    this.creator = creator;
  }

  public JsonObject getOriginalCreator() {
    return originalCreator;
  }

  public void setOriginalCreator(JsonObject originalCreator) {
    this.originalCreator = originalCreator;
  }

  public CourseStatisticsEo getStatistics() {
    return statistics;
  }

  public void setStatistics(CourseStatisticsEo statistics) {
    this.statistics = statistics;
  }

  public Boolean getVisibleOnProfile() {
    return visibleOnProfile;
  }

  public void setVisibleOnProfile(Boolean visibleOnProfile) {
    this.visibleOnProfile = visibleOnProfile;
  }

  public String getModifierId() {
    return modifierId;
  }

  public void setModifierId(String modifierId) {
    this.modifierId = modifierId;
  }

  public String getSubjectBucket() {
    return subjectBucket;
  }

  public void setSubjectBucket(String subjectBucket) {
    this.subjectBucket = subjectBucket;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getSubjectSequence() {
    return subjectSequence;
  }

  public void setSubjectSequence(int subjectSequence) {
    this.subjectSequence = subjectSequence;
  }

  public int getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(int sequenceId) {
    this.sequenceId = sequenceId;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public String getPublishStatus() {
    return publishStatus;
  }

  public void setPublishStatus(String publishStatus) {
    this.publishStatus = publishStatus;
  }

  public int getIsFeatured() {
    return isFeatured;
  }

  public void setIsFeatured(int isFeatured) {
    this.isFeatured = isFeatured;
  }

  public JsonObject getTaxonomy() {
    return taxonomy;
  }

  public void setTaxonomy(JsonObject taxonomy) {
    this.taxonomy = taxonomy;
  }

  public JsonObject getLicense() {
    return license;
  }

  public void setLicense(JsonObject license) {
    this.license = license;
  }

  public String getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(String publishDate) {
    this.publishDate = publishDate;
  }

}
