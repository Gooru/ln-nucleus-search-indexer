package org.gooru.nucleus.search.indexers.app.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PCWeightUtil {

  protected static final Logger LOGGER = LoggerFactory.getLogger(PCWeightUtil.class);

  private PCWeightUtil() {
    throw new AssertionError();
  }

  public static double getResourcePcWeight(final ScoreFields rankingData) throws Exception {
    try {
      float usedInSCollectionCount = (float) rankingData.getResourceUsedCollectionCount() / ScoreConstants.MAX_RESOURCE_USED_99PERSENT_VAL;
      float frameBreakerScore = (rankingData.getHasFrameBreaker() == 0) ? 1f : ScoreConstants.DEMOTE_FRAME_BREAKER;
      float thumbnailScore = (rankingData.getHasNoThumbnail() == 0) ? 1f : ScoreConstants.DEMOTE_THUMBNAIL;
      float descScore = computeDescriptionValue(rankingData.getDescription(), rankingData.getHasNoDescription());
      float domainBoost = (rankingData.getDomainBoost() == 1) ? 1f : ScoreConstants.DEMOTE_DOMAIN;
      float standardScore = (rankingData.getHasNoStandard() == 0) ? 1f : 0f;
      float oerScore = (rankingData.getOer() == 1) ? 1f : 0f;
      float skillScore = (rankingData.getHas21stCenturySkills()) ? 1f : 0f;
      float viewsScore = rankingData.getViewsCount() / ScoreConstants.MAX_RESOURCE_VIEWS_99PERSENT_VAL;
      float publishStatusScore = (rankingData.getIsPublished() == 1) ? 1f : 0f;
      double publisherQualityIndicator = ((double) normalizeValueToFive(rankingData.getPublisherQualityIndicator()) / 5);
      double contentQualityIndicator = ((double) normalizeValueToFive(rankingData.getContentQualityIndicator()) / 5);

      double usageSignalWeight = (double) (((double) (normalizeValue(usedInSCollectionCount) + normalizeValue(viewsScore)) / 2) * 0.5);
      double otherSignalWeight = (double) (((double) (descScore + frameBreakerScore + thumbnailScore + standardScore + domainBoost + skillScore + oerScore + publishStatusScore) / 8) * 0.2);
      double editorialTagSignals = (double) (((double) (publisherQualityIndicator + contentQualityIndicator) / 2) * 0.3);
      return (double) normalizeValue(usageSignalWeight + otherSignalWeight + editorialTagSignals);
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  public static double getCollectionPCWeight(final ScoreFields rankingData) throws Exception {

    try {
      Serializable scollectionScoreCompiled = MVEL.compileExpression(ScoreConstants.COLLECTION_SCORE_EXPRESSION);

      Map<String, Object> scollectionMvelInputs = new HashMap<>();
      scollectionMvelInputs.put("viewsCount", rankingData.getViewsCount());
      scollectionMvelInputs.put("hasNoDescription", rankingData.getHasNoDescription());
      //	scollectionMvelInputs.put("descriptionLength", (StringUtils.trimToNull(rankingData.getDescription()) != null) ? rankingData.getDescription
      // ().length() : 0);
      scollectionMvelInputs.put("hasNoThumbnail", rankingData.getHasNoThumbnail());
      scollectionMvelInputs.put("hasNoStandard", rankingData.getHasNoStandard());
      scollectionMvelInputs.put("isCopied", rankingData.getIsCopied());
      scollectionMvelInputs.put("collectionItemCount", rankingData.getQuestionCount() + rankingData.getResouceCount());
      scollectionMvelInputs.put("questionCount", rankingData.getQuestionCount());
      scollectionMvelInputs.put("resourceCount", rankingData.getResouceCount());
      scollectionMvelInputs.put("maxViewCount", ScoreConstants.MAX_COLLECTION_VIEWS_99PERSENT_VAL);
      scollectionMvelInputs.put("isPublished", rankingData.getIsPublished());
      VariableResolverFactory inputFactory = new MapVariableResolverFactory(scollectionMvelInputs);
      Double scPreComputedWeight = 0.0;
      scPreComputedWeight = (Double) MVEL.executeExpression(scollectionScoreCompiled, inputFactory);
      return scPreComputedWeight;
    } catch (Exception e) {
      throw new Exception(e);
    }

  }

  protected static Float computeDescriptionValue(String description, int hasNoDescription) {
    Float descriptionScore = -5.0f;
    if (hasNoDescription == 0) {
      descriptionScore = 0.8f;
      if (description.length() > 80) {
        descriptionScore += 0.2f;
      }
    }
    return descriptionScore;
  }

  private static Float normalizeValue(Float score) {
    if (score > 1) {
      score = 1.0F;
    } else if (score < 0) {
      score = 0.0F;
    }
    return score;
  }
  
  private static Double normalizeValue(Double score) {
    if (score > 1) {
      score = 1.0;
    } else if (score < 0) {
      score = 0.0;
    }
    return score;
  }
  
  private static double normalizeValueToFive(double score) {
    if (score > 5) {
      score = 5.0;
    } else if (score < 0) {
      score = 0.0;
    }
    return score;
  }

}

