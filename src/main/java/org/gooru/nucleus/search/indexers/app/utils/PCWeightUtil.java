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
      float usedInSCollectionCount =
        (float) rankingData.getResourceUsedCollectionCount() / ScoreConstants.MAX_RESOURCE_USED_99PERSENT_VAL;
      float frameBreakerScore = (!rankingData.getHasFrameBreaker()) ? 1f : ScoreConstants.DEMOTE_FRAME_BREAKER;
      float thumbnailScore = (rankingData.getHasNoThumbnail() == 0) ? 1f : ScoreConstants.DEMOTE_THUMBNAIL;
      float descScore = computeDescriptionValue(rankingData.getDescription(), rankingData.getHasNoDescription());
      float domainBoost = (rankingData.getDomainBoost() == 1) ? 1f : ScoreConstants.DEMOTE_DOMAIN;
      float standardScore = (rankingData.getHasNoStandard() == 0) ? 1f : 0f;
      float skillScore = (rankingData.getHas21stCenturySkills()) ? 0.5f : 0f;
      float viewsScore = rankingData.getViewsCount() / ScoreConstants.MAX_RESOURCE_VIEWS_99PERSENT_VAL;
      float publishStatusScore = (rankingData.getIsPublished() == 1) ? 1f : 0f;
      Double publisherQualityIndicator = (rankingData.getPublisherQualityIndicator() != null)
        ? ((double) normalizeValueToFive(rankingData.getPublisherQualityIndicator()) / 5) : null;
      Double contentQualityIndicator = (rankingData.getContentQualityIndicator() != null)
        ? ((double) normalizeValueToFive(rankingData.getContentQualityIndicator()) / 5) : null;
      float featuredScore = (rankingData.isFeatured()) ? 1f : 0f;
      Double efficacy = (rankingData.getEfficacy() != null) ? ((double) rankingData.getEfficacy()) : 0;
      Double engagement = (rankingData.getEngagement() != null) ? ((double) rankingData.getEngagement()) : 0;
      Double relevance = (rankingData.getRelevance() != null) ? ((double) rankingData.getRelevance()) : 0;
      float usedInLibrary = (rankingData.isUsedInLibrary()) ? 1f : 0f;

      double usageSignalWeight =
        (double) (((double) (normalizeValue(usedInSCollectionCount) + normalizeValue(viewsScore)) / 2) * 0.2);
      double contentQualitySignalWeight = (double) (((double) (descScore + frameBreakerScore + thumbnailScore
        + domainBoost + skillScore + publishStatusScore + featuredScore + usedInLibrary) / 8) * 0.2);
      double reefWeight = (double) (((double) (efficacy + engagement + relevance) / 1.8) * 0.2);
      double editorialTagSignals = 0;
      if (contentQualityIndicator != null) {
        editorialTagSignals = contentQualityIndicator * 0.2;
      } else if (publisherQualityIndicator != null) {
        editorialTagSignals = publisherQualityIndicator * 0.2;
      }
      double relevanceSignal = standardScore * 0.2;
      
      return (double) normalizeValue(
        usageSignalWeight + contentQualitySignalWeight + editorialTagSignals + reefWeight + relevanceSignal);
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
      scollectionMvelInputs.put("isFeatured", rankingData.isFeatured());
      scollectionMvelInputs.put("isTeacherGradingType", rankingData.getGradingType() != null && rankingData.getGradingType().equalsIgnoreCase(ScoreConstants.TEACHER) ? 1 : 0);
      scollectionMvelInputs.put("efficacy", rankingData.getEfficacy());
      scollectionMvelInputs.put("engagement", rankingData.getEngagement());
      scollectionMvelInputs.put("relevance", rankingData.getRelevance());
      scollectionMvelInputs.put("isUsedInLibrary", rankingData.isUsedInLibrary());
      VariableResolverFactory inputFactory = new MapVariableResolverFactory(scollectionMvelInputs);
      Double scPreComputedWeight = 0.0;
      scPreComputedWeight = (Double) MVEL.executeExpression(scollectionScoreCompiled, inputFactory);
      return scPreComputedWeight;
    } catch (Exception e) {
      throw new Exception(e);
    }

  }

  protected static Float computeDescriptionValue(String description, int hasNoDescription) {
    Float descriptionScore = -2.0f;
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

