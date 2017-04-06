package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.constants.IndexFields;

import io.vertx.core.json.JsonObject;

public class CourseStatisticsEo extends JsonObject {
  
  public int getUnitCount() {
    return this.getInteger(IndexFields.UNIT_COUNT, 0);
  }

  public void setUnitCount(int unitCount) {
    this.put(IndexFields.UNIT_COUNT, unitCount);
  }

  public int getCollaboratorCount() {
    return this.getInteger(IndexFields.COLLABORATOR_COUNT, 0);
  }

  public void setCollaboratorCount(int collaboratorCount) {
    this.put(IndexFields.COLLABORATOR_COUNT, collaboratorCount);
  }

  public long getViewsCount() {
    return this.getLong(IndexFields.VIEWS_COUNT, 0l);
  }

  public void setViewsCount(long viewsCount) {
    this.put(IndexFields.VIEWS_COUNT, viewsCount);
  }

  public long getCourseRemixCount() {
    return this.getLong(IndexFields.COURSE_REMIXCOUNT, 0l);
  }

  public void setCourseRemixCount(long courseRemixCount) {
    this.put(IndexFields.COURSE_REMIXCOUNT, courseRemixCount);
  }

  public double getPreComputedWeight() {
    return this.getDouble(IndexFields.PCWEIGHT, 0.0);
  }

  public void setPreComputedWeight(double preComputedWeight) {
    this.put(IndexFields.PCWEIGHT, preComputedWeight);
  }
  
  public Boolean isFeatured() {
    return this.getBoolean("isFeatured", false);
  }

  public void setFeatured(Boolean isFeatured) {
    if (isFeatured == null) {
      isFeatured = false;
    }
    this.put("isFeatured", isFeatured);
  }
  
  public Integer getCollectionCount() {
    return this.getInteger("collectionCount", 0);
  }

  public void setCollectionCount(Integer collectionCount) {
    if (collectionCount == null) {
      collectionCount = 0;
    }
    this.put("collectionCount", collectionCount);
  }

  public Integer getAssessmentCount() {
    return this.getInteger("assessmentCount", 0);
  }

  public void setAssessmentCount(Integer assessmentCount) {
    if (assessmentCount == null) {
      assessmentCount = 0;
    }
    this.put("assessmentCount", assessmentCount);
  }
  
  public Integer getExternalAsssessmentCount() {
    return this.getInteger("externalAssessmentCount", 0);
  }

  public void setExternalAssessmentCount(Integer externalAssessmentCount) {
    if (externalAssessmentCount == null) {
      externalAssessmentCount = 0;
    }
    this.put("externalAssessmentCount", externalAssessmentCount);
  }
  
  
  public Integer getContainingCollectionsCount() {
    return this.getInteger("containingCollectionsCount", 0);
  }

  public void setContainingCollectionsCount(Integer containingCollectionsCount) {
    if (containingCollectionsCount == null) {
      containingCollectionsCount = 0;
    }
    this.put("containingCollectionsCount", containingCollectionsCount);
  }
  
  public Integer getLessonCount() {
    return this.getInteger("lessonCount", 0);
  }

  public void setLessonCount(Integer lessonCount) {
    if (lessonCount == null) {
      lessonCount = 0;
    }
    this.put("lessonCount", lessonCount);
  }
}
