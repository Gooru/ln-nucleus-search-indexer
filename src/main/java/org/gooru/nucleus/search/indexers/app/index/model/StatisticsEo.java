package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

public class StatisticsEo {

  private JsonObject statistics = null;

  public StatisticsEo() {
    this.statistics = new JsonObject();
  }

  public JsonObject getStatistics() {
    return statistics;
  }

  public Long getViewsCount() {
    return statistics.getLong("viewsCount", 0L);
  }

  public void setViewsCount(Long viewsCount) {
    if (viewsCount == null) {
      viewsCount = 0L;
    }
    this.statistics = JsonUtil.set(statistics, "viewsCount", viewsCount);
  }

  public Integer getUsedInCollectionCount() {
    return statistics.getInteger("usedInCollectionCount", 0);
  }

  public void setUsedInCollectionCount(Integer usedInCollectionCount) {
    if (usedInCollectionCount == null) {
      usedInCollectionCount = 0;
    }
    this.statistics = JsonUtil.set(statistics, "usedInCollectionCount", usedInCollectionCount);
  }

  public Integer getInvalidResource() {
    return statistics.getInteger("invalidResource", 0);
  }

  public void setInvalidResource(Integer invalidResource) {
    if (invalidResource == null) {
      invalidResource = 0;
    }
    this.statistics = JsonUtil.set(statistics, "invalidResource", invalidResource);
  }

  public Integer getHasNoThumbnail() {
    return statistics.getInteger("hasNoThumbnail", 0);
  }

  public void setHasNoThumbnail(Integer hasNoThumbnail) {
    if (hasNoThumbnail == null) {
      hasNoThumbnail = 0;
    }
    this.statistics = JsonUtil.set(statistics, "hasNoThumbnail", hasNoThumbnail);
  }

  public Integer getHasNoDescription() {
    return statistics.getInteger("hasNoDescription", 0);
  }

  public void setHasNoDescription(Integer hasNoDescription) {
    if (hasNoDescription == null) {
      hasNoDescription = 0;
    }
    this.statistics = JsonUtil.set(statistics, "hasNoDescription", hasNoDescription);
  }


  public Integer getHasFrameBreaker() {
    return statistics.getInteger("hasFrameBreaker", 0);
  }

  public void setHasFrameBreaker(Integer hasFrameBreaker) {
    if (hasFrameBreaker == null) {
      hasFrameBreaker = 0;
    }
    this.statistics = JsonUtil.set(statistics, "hasFrameBreaker", hasFrameBreaker);
  }

  public Integer getStatusIsBroken() {
    return statistics.getInteger("statusIsBroken", 0);
  }

  public void setStatusIsBroken(Integer statusIsBroken) {
    if (statusIsBroken == null) {
      statusIsBroken = 0;
    }
    this.statistics = JsonUtil.set(statistics, "statusIsBroken", statusIsBroken);
  }

  public Double getPreComputedWeight() {
    return statistics.getDouble("preComputedWeight", 0.0);
  }

  public void setPreComputedWeight(Double preComputedWeight) {
    if (preComputedWeight == null) {
      preComputedWeight = 0.0;
    }
    this.statistics = JsonUtil.set(statistics, "preComputedWeight", preComputedWeight);
  }

  public Long getAverageTimeSpent() {
    return statistics.getLong("averageTimeSpent", 0L);
  }

  public void setAverageTimeSpent(Long averageTimeSpent) {
    if (averageTimeSpent == null) {
      averageTimeSpent = 0L;
    }
    this.statistics = JsonUtil.set(statistics, "averageTimeSpent", averageTimeSpent);
  }

  public Long getResourceUsedUserCount() {
    return statistics.getLong("resourceUsedUserCount", 0L);
  }

  public void setResourceUsedUserCount(Long resourceUsedUserCount) {
    if (resourceUsedUserCount == null) {
      resourceUsedUserCount = 0L;
    }
    this.statistics = JsonUtil.set(statistics, "resourceUsedUserCount", resourceUsedUserCount);
  }

  public Long getResourceAddedCount() {
    return statistics.getLong("resourceAddedCount", 0L);
  }

  public void setResourceAddedCount(Long resourceAddedCount) {
    if (resourceAddedCount == null) {
      resourceAddedCount = 0L;
    }
    this.statistics = JsonUtil.set(statistics, "resourceAddedCount", resourceAddedCount);
  }

  public Integer getCollectionRemixCount() {
    return statistics.getInteger("collectionRemixCount", 0);
  }

  public void setCollectionRemixCount(Integer collectionRemixCount) {
    if (collectionRemixCount == null) {
      collectionRemixCount = 0;
    }
    this.statistics = JsonUtil.set(statistics, "collectionRemixCount", collectionRemixCount);
  }

  public Boolean getHas21stCenturySkills() {
    return statistics.getBoolean("has21stCenturySkills", false);
  }

  public void setHas21stCenturySkills(Boolean has21stCenturySkills) {
    if (has21stCenturySkills == null) {
      has21stCenturySkills = false;
    }
    this.statistics = JsonUtil.set(statistics, "has21stCenturySkills", has21stCenturySkills);
  }

  public Boolean getHasAdvertisement() {
    return statistics.getBoolean("hasAdvertisement", false);
  }

  public void setHasAdvertisement(Boolean hasAdvertisement) {
    if (hasAdvertisement == null) {
      hasAdvertisement = false;
    }
    this.statistics = JsonUtil.set(statistics, "hasAdvertisement", hasAdvertisement);
  }

  public Integer getQuestionCount() {
    return statistics.getInteger("questionCount", 0);
  }

  public void setQuestionCount(Integer questionCount) {
    if (questionCount == null) {
      questionCount = 0;
    }
    this.statistics = JsonUtil.set(statistics, "questionCount", questionCount);
  }

  public Integer getResourceCount() {
    return statistics.getInteger("resourceCount", 0);
  }

  public void setResourceCount(Integer resourceCount) {
    if (resourceCount == null) {
      resourceCount = 0;
    }
    this.statistics = JsonUtil.set(statistics, "resourceCount", resourceCount);
  }

  public Integer getCollaboratorCount() {
    return statistics.getInteger("collaboratorCount", 0);
  }

  public void setCollaboratorCount(Integer collaboratorCount) {
    if (collaboratorCount == null) {
      collaboratorCount = 0;
    }
    this.statistics = JsonUtil.set(statistics, "collaboratorCount", collaboratorCount);
  }

  public Integer getContentCount() {
    return statistics.getInteger("contentCount", 0);
  }

  public void setContentCount(Integer contentCount) {
    if (contentCount == null) {
      contentCount = 0;
    }
    this.statistics = JsonUtil.set(statistics, "contentCount", contentCount);
  }
  
  public Integer getContentQualityIndicator() {
    return statistics.getInteger("contentQualityIndicator", 0);
  }

  public void setContentQualityIndicator(Integer contentQualityIndicator) {
    if (contentQualityIndicator == null) {
      contentQualityIndicator = 0;
    }
    this.statistics = JsonUtil.set(statistics, "contentQualityIndicator", contentQualityIndicator);
  }
  
  public Integer getPublisherQualityIndicator() {
    return statistics.getInteger("publisherQualityIndicator", 0);
  }

  public void setPublisherQualityIndicator(Integer publisherQualityIndicator) {
    if (publisherQualityIndicator == null) {
      publisherQualityIndicator = 0;
    }
    this.statistics = JsonUtil.set(statistics, "publisherQualityIndicator", publisherQualityIndicator);
  }
}
