package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

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
    return this.getBoolean(IndexFields.IS_FEATURED, false);
  }

  public void setFeatured(Boolean isFeatured) {
    if (isFeatured == null) {
      isFeatured = false;
    }
    this.put(IndexFields.IS_FEATURED, isFeatured);
  }
  
  public Long getCollectionCount() {
    return this.getLong(IndexFields.COLLECTION_COUNT, 0L);
  }

  public void setCollectionCount(Long collectionCount) {
    if (collectionCount == null) {
      collectionCount = 0L;
    }
    this.put(IndexFields.COLLECTION_COUNT, collectionCount);
  }

  public Long getAssessmentCount() {
    return this.getLong(IndexFields.ASSESMENT_COUNT, 0L);
  }

  public void setAssessmentCount(Long assessmentCount) {
    if (assessmentCount == null) {
      assessmentCount = 0L;
    }
    this.put(IndexFields.ASSESMENT_COUNT, assessmentCount);
  }
  
  public Long getExternalAsssessmentCount() {
    return this.getLong(IndexFields.EXTERNAL_ASSESSMENT_COUNT, 0L);
  }

  public void setExternalAssessmentCount(Long externalAssessmentCount) {
    if (externalAssessmentCount == null) {
      externalAssessmentCount = 0L;
    }
    this.put(IndexFields.EXTERNAL_ASSESSMENT_COUNT, externalAssessmentCount);
  }
  
  
  public Long getContainingCollectionsCount() {
    return this.getLong(IndexFields.CONTAINING_COLLECTIONS_COUNT, 0L);
  }

  public void setContainingCollectionsCount(Long containingCollectionsCount) {
    if (containingCollectionsCount == null) {
      containingCollectionsCount = 0L;
    }
    this.put(IndexFields.CONTAINING_COLLECTIONS_COUNT, containingCollectionsCount);
  }
  
  public Long getLessonCount() {
    return this.getLong(IndexFields.LESSON_COUNT, 0L);
  }

  public void setLessonCount(Long lessonCount) {
    if (lessonCount == null) {
      lessonCount = 0L;
    }
    this.put(IndexFields.LESSON_COUNT, lessonCount);
  }
  
  public Long getRemixedInClassCount() {
    return this.getLong(IndexFields.REMIXED_IN_CLASS_COUNT, 0L);
  }

  public void setRemixedInClassCount(Long remixedInClassCount) {
    if (remixedInClassCount == null) {
      remixedInClassCount = 0L;
    }
    this.put(IndexFields.REMIXED_IN_CLASS_COUNT, remixedInClassCount);
  }
  
  public Long getUsedByStudentCount() {
    return this.getLong(IndexFields.USED_BY_STUDENT_COUNT, 0L);
  }

  public void setUsedByStudentCount(Long usedByStudentCount) {
    if (usedByStudentCount == null) {
      usedByStudentCount = 0L;
    }
    this.put(IndexFields.USED_BY_STUDENT_COUNT, usedByStudentCount);
  }
  

  public Double getEfficacy() {
    return this.getDouble(IndexFields.EFFICACY, 0.5);
  }

  public void setEfficacy(Double efficacy) {
    if (efficacy == null) {
      efficacy = 0.5;
    }
    this.put(IndexFields.EFFICACY, efficacy);
  }
  

  public Double getEngagement() {
    return this.getDouble(IndexFields.ENGAGEMENT, 0.5);
  }

  public void setEngagement(Double engagement) {
    if (engagement == null) {
      engagement = 0.5;
    }
    this.put(IndexFields.ENGAGEMENT, engagement);
  }
  

  public Double getRelevance() {
    return this.getDouble(IndexFields.RELEVANCE, 0.5);
  }

  public void setRelevance(Double relevance) {
    if (relevance == null) {
      relevance = 0.5;
    }
    this.put(IndexFields.RELEVANCE, relevance);
  }
  
  public Boolean isLibraryContent() {
    return this.getBoolean("isLibraryContent", false);
  }

  public void setLibraryContent(Boolean isLibraryContent) {
    if (isLibraryContent == null) {
        isLibraryContent = false;
    }
    this.put("isLibraryContent", isLibraryContent);
  }
  
  public Boolean isLMContent() {
      return this.getBoolean("isLMContent", false);
  }

  public void setLMContent(Boolean isLMContent) {
      if (isLMContent == null) {
          isLMContent = false;
      }
      this.put("isLMContent", isLMContent);
  }
  
  public void setCollaboratorCount(Integer collaboratorCount) {
    if (collaboratorCount == null) {
      collaboratorCount = 0;
    }
    this.put("collaboratorCount", collaboratorCount);
  }
}
