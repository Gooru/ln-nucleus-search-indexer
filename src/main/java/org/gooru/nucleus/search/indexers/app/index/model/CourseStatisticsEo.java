package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonObject;

public class CourseStatisticsEo extends JsonObject {

  private int unitCount = 0;
  
  private int collaboratorCount = 0;
  
  private long viewsCount = 0;
  
  private long courseRemixCount = 0;
  
  private double preComputedWeight = 0.0;

  public int getUnitCount() {
    return unitCount;
  }

  public void setUnitCount(int unitCount) {
    this.unitCount = unitCount;
  }

  public int getCollaboratorCount() {
    return collaboratorCount;
  }

  public void setCollaboratorCount(int collaboratorCount) {
    this.collaboratorCount = collaboratorCount;
  }

  public long getViewsCount() {
    return viewsCount;
  }

  public void setViewsCount(long viewsCount) {
    this.viewsCount = viewsCount;
  }

  public long getCourseRemixCount() {
    return courseRemixCount;
  }

  public void setCourseRemixCount(long courseRemixCount) {
    this.courseRemixCount = courseRemixCount;
  }

  public double getPreComputedWeight() {
    return preComputedWeight;
  }

  public void setPreComputedWeight(double preComputedWeight) {
    this.preComputedWeight = preComputedWeight;
  }
}
