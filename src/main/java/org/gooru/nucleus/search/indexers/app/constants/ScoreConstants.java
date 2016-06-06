package org.gooru.nucleus.search.indexers.app.constants;

public final class ScoreConstants {

  public static final String VIEW_COUNT = "viewsCount";
  public static final String HAS_21ST_CENTURY_SKILL = "has21stCenturySkills";
  public static final String USED_IN_COLLECTION_COUNT = "usedInCollectionCount";
  public static final String HAS_FRAME_BREAKER = "hasFrameBreaker";
  public static final String PC_WEIGHT = "preComputedWeight";
  public static final String HAS_NO_THUMBNAIL = "hasNoThumbnail";
  public static final String SATS_HAS_NO_DESC = "hasNoDescription";
  public static final String TAX_HAS_NO_STANDARD = "hasNoStandard";
  public static final String RESOURCE_COUNT = "resourceCount";
  public static final String QUESTION_COUNT = "questionCount";
  public static final String COLLAB_COUNT = "collaboratorCount";
  public static final String COLLECTION_REMIX_COUNT = "collectionRemixCount";
  public static final String TAXONOMY_FIELD = "taxonomy";
  public static final String STATISTICS_FIELD = "statistics";
  public static final String DESCRIPTION_FIELD = "description";
  public static final String RESOURCE_URL_FIELD = "url";
  public static final String ORIGINAL_CONTENT_FIELD = "originalContentId";
  public static final String OPERATION_TYPE_INCR = "increaseCount";
  public static final String OPERATION_TYPE_DECR = "decreaseCount";
  public static final String OPERATION_TYPE_UPDATE = "update";
  public static final String VIEWS_COUNT_FIELD = "statistics.viewsCount";
  public static final String OER = "oer";
  public static final String PUBLISH_STATUS = "publishStatus";
  public static final String LEARNING_OBJ = "learningObjective";

  public static final String[] DEMOTE_DOMAINS = {"wikipedia"};

  //Score formula collection
  public static final String COLLECTION_SCORE_EXPRESSION =
    "(((viewsCount != null && viewsCount != 0)? ((viewsCount/maxViewCount) * 2.5) : 0.0)+((hasNoThumbnail != null ) ? ((hasNoThumbnail > 0)? -1.0 :" +
      " 1.0) : 0.0)+((hasNoDescription != null)? ((hasNoDescription > 0)? -2.0 : 2.2):0.0)+((isCopied != null)? ((isCopied > 0)?  0.0 : 1.4):0.0)+(" +
      "(hasNoStandard != null)? ((hasNoStandard > 0 )? 0.0 : 1.4) : 0.0)+((resourceCount <= 2 || resourceCount > 8 )? 0.0 :2.0)+((questionCount == " +
      "0)? 0.0:1.7)+((collectionItemCount < 1 ? -10.0 : 0.0)) +((isPublished == 1)? 2.5 : 0.0))";

  // Score max/99 percentile values
  public static final int MAX_RESOURCE_USED_99PERSENT_VAL = 25;
  public static final int MAX_RESOURCE_VIEWS_99PERSENT_VAL = 200;
  public static final int MAX_COLLECTION_VIEWS_99PERSENT_VAL = 100;
  public static final int MAX_COLLECTION_RESOURCE_COUNT_99PERSENT_VAL = 20;
  public static final int MAX_COLLECTION_QUESTION_COUNT_99PERSENT_VAL = 20;


  public static final float DEMOTE_FRAME_BREAKER = -5.0f;
  public static final float DEMOTE_THUMBNAIL = -1.0f;
  public static final float DEMOTE_DOMAIN = -3.0f;


  private ScoreConstants() {
    throw new AssertionError();
  }
}
