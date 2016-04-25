package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ScoreFields {

  private Long viewsCount = 0L;

  private int resourceUsedCollectionCount = 0;

  private int resouceCount = 0;

  private int questionCount = 0;

  private int hasFrameBreaker = 0;

  private int hasNoThumbnail = 0;

  private int hasNoStandard = 0;

  private boolean has21stCenturySkills;

  private String description;

  private int domainBoost = 1;

  private int hasNoDescription = 0;

  private int isCopied = 0;

  private String url;
  
  private int oer = 0;

  public ScoreFields(Map<String, Object> scoreFieldsMap) {
    if (scoreFieldsMap.get(ScoreConstants.VIEW_COUNT) != null) {
      this.viewsCount = (Long) scoreFieldsMap.get(ScoreConstants.VIEW_COUNT);
    }
    if (scoreFieldsMap.get(ScoreConstants.USED_IN_COLLECTION_COUNT) != null) {
      this.resourceUsedCollectionCount = (int) scoreFieldsMap.get(ScoreConstants.USED_IN_COLLECTION_COUNT);
    }

    if (scoreFieldsMap.get(ScoreConstants.RESOURCE_COUNT) != null) {
      this.resouceCount = (int) scoreFieldsMap.get(ScoreConstants.RESOURCE_COUNT);
    }

    if (scoreFieldsMap.get(ScoreConstants.QUESTION_COUNT) != null) {
      this.questionCount = (int) scoreFieldsMap.get(ScoreConstants.QUESTION_COUNT);
    }

    if (scoreFieldsMap.get(ScoreConstants.HAS_FRAME_BREAKER) != null) {
      this.hasFrameBreaker = (int) scoreFieldsMap.get(ScoreConstants.HAS_FRAME_BREAKER);
    }

    if (scoreFieldsMap.get(ScoreConstants.HAS_NO_THUMBNAIL) != null) {
      this.hasNoThumbnail = (int) scoreFieldsMap.get(ScoreConstants.HAS_NO_THUMBNAIL);
    }

    if (scoreFieldsMap.get(ScoreConstants.TAX_HAS_STANDARD) != null) {
      this.hasNoStandard = (int) scoreFieldsMap.get(ScoreConstants.TAX_HAS_STANDARD);
    }

    if (scoreFieldsMap.get(ScoreConstants.HAS_21ST_CENTURY_SKILL) != null) {
      this.has21stCenturySkills = (boolean) scoreFieldsMap.get(ScoreConstants.HAS_21ST_CENTURY_SKILL);
    }

    if (scoreFieldsMap.get(ScoreConstants.RESOURCE_URL_FIELD) != null) {
      this.url = (String) scoreFieldsMap.get(ScoreConstants.RESOURCE_URL_FIELD);
    }

    if (scoreFieldsMap.get(ScoreConstants.SATS_HAS_NO_DESC) != null) {
      this.hasNoDescription = (int) scoreFieldsMap.get(ScoreConstants.SATS_HAS_NO_DESC);
    }

    if (scoreFieldsMap.get(ScoreConstants.DESCRIPTION_FIELD) != null) {
      this.description = (String) scoreFieldsMap.get(ScoreConstants.DESCRIPTION_FIELD);
    }

    if (scoreFieldsMap.get(ScoreConstants.ORIGINAL_CONTENT_FIELD) != null) {
      this.isCopied = 1;
    }
    
    if (scoreFieldsMap.get(ScoreConstants.OER) != null) {
      this.oer = (int) scoreFieldsMap.get(ScoreConstants.OER);
    }

  }

  public static String getDomainName(String url) throws URISyntaxException {
    URI uri = new URI(url);
    String domain = uri.getHost();
    return domain.startsWith("www.") ? domain.substring(4) : domain;
  }

  public Long getViewsCount() {
    return viewsCount;
  }

  public int getResourceUsedCollectionCount() {
    return resourceUsedCollectionCount;
  }

  public int getResouceCount() {
    return resouceCount;
  }

  public int getQuestionCount() {
    return questionCount;
  }

  public int getCollectionItemCount() {
    return 0;
  }

  public int getHasFrameBreaker() {
    return hasFrameBreaker;
  }

  public int getHasNoThumbnail() {
    return hasNoThumbnail;
  }

  public int getHasNoStandard() {
    return hasNoStandard;
  }

  public boolean getHas21stCenturySkills() {
    return has21stCenturySkills;
  }

  public String getDescription() {
    return description;
  }

  public int getDomainBoost() {
    for (int domainIndex = 0; domainIndex < ScoreConstants.DEMOTE_DOMAINS.length; domainIndex++) {
      try {
        if (getUrl() != null && ScoreConstants.DEMOTE_DOMAINS[domainIndex].contains(getDomainName(getUrl()))) {
          domainBoost = 0;
        }
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
    return domainBoost;
  }

  public int getHasNoDescription() {
    return hasNoDescription;
  }

  public int getIsCopied() {
    return isCopied;
  }

  public String getUrl() {
    return url;
  }

  public int getOer() {
    return oer;
  }
}
