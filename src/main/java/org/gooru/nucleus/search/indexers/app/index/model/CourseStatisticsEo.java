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
}
