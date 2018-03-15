package org.gooru.nucleus.search.indexers.app.builders;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.AnswerEo;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEio;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.HintEo;
import org.gooru.nucleus.search.indexers.app.index.model.QuestionEo;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 */
public class QuestionEsIndexSrcBuilder<S extends JsonObject, D extends ContentEio> extends ContentEsIndexSrcBuilder<S, D> {

  @Override
  public JsonObject build(JsonObject source, D contentEo) throws Exception {
    try {
      super.build(source, contentEo);

      String contentFormat = source.getString(EntityAttributeConstants.CONTENT_FORMAT, null);
      contentEo.setContentFormat(contentFormat);
      contentEo.setOriginalContentId(source.getString(EntityAttributeConstants.ORIGINAL_CONTENT_ID, null));
      contentEo.setParentContentId(source.getString(EntityAttributeConstants.PARENT_CONTENT_ID, null));
      contentEo.setCollectionId(source.getString(EntityAttributeConstants.COLLECTION_ID, null));

      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo orginalCreatorEo = new UserEo();
        JsonObject creator = getUserRepo().getUser(originalCreatorId);
        if (creator != null && !creator.isEmpty()) {
          setUser(creator, orginalCreatorEo);
          contentEo.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      
      // Set Metadata
      String metadataString = source.getString(EntityAttributeConstants.METADATA, null);
      JsonObject metadata = null;
      if (StringUtils.isNotBlank(metadataString) && !metadataString.equalsIgnoreCase(IndexerConstants.STR_NULL)) metadata = new JsonObject(metadataString);
      JsonObject dataMap = setMetaData(metadata);
      if (dataMap != null && !dataMap.isEmpty()) contentEo.setMetadata(dataMap);
      
      // Set Question
      QuestionEo questionEo = new QuestionEo();
       
      if(contentFormat != null && contentFormat.equalsIgnoreCase(IndexerConstants.TYPE_QUESTION)){
        questionEo.setQuestionText(contentEo.getDescription());
      }
      
      String answerJson = source.getString(EntityAttributeConstants.ANSWER, null);
      if (answerJson != null && !answerJson.equalsIgnoreCase(IndexerConstants.STR_NULL)) {
        JsonArray answerArray = new JsonArray(answerJson);
        if (answerArray != null && answerArray.size() > 0) {
          AnswerEo answerEo = new AnswerEo();
          StringBuilder answerText = new StringBuilder();
          for (int index = 0; index < answerArray.size(); index++) {
            JsonObject answerObject = answerArray.getJsonObject(index);
            String answerString = answerObject.getString(EntityAttributeConstants.ANSWER_TEXT, null);
            if (answerString != null) {
              if (answerText.length() > 0) {
                answerText.append(" ~~ ");
              }
              answerText.append(answerString);
            }
          }
          answerEo.setAnswerText(answerText.toString());
          questionEo.setAnswer(answerEo.getAnswer());
        }
      }
      String hintDetail = source.getString(EntityAttributeConstants.HINT_EXPLANATION_DETAIL, null);
      if (hintDetail != null && !hintDetail.equalsIgnoreCase(IndexerConstants.STR_NULL)) {
        JsonObject hintExplanationDetail = new JsonObject(hintDetail);
        if (hintExplanationDetail != null) {
          JsonArray hintArray = hintExplanationDetail.getJsonArray(EntityAttributeConstants.HINT, null);
          if (hintArray != null && hintArray.size() > 0) {
            HintEo hintEo = new HintEo();
            StringBuilder hintText = new StringBuilder();
            int hintCount = 0;
            for (int index = 0; index < hintArray.size(); index++) {
              JsonObject hintObject = hintArray.getJsonObject(index);
              String hint = hintObject.getString(EntityAttributeConstants.HINT_TEXT, null);
              if (hint != null) {
                hintCount++;
                if (hintText.length() > 0) {
                  hintText.append(" ~~ ");
                }
                hintText.append(hint);
              }
            }
            hintEo.setHintText(hintText.toString());
            hintEo.setHintCount(hintCount);
            questionEo.setHint(hintEo.getHint());
          }
        }
      }
      if (!questionEo.getQuestion().isEmpty()) {
        contentEo.setQuestion(questionEo.getQuestion());
      }

      //Set CUL course mapped
      CourseEo course = new CourseEo(); 
      course.setId(source.getString(IndexerConstants.RESOURCE_COURSE_ID, null));
      course.setTitle(source.getString(IndexerConstants.RESOURCE_COURSE, null));
      contentEo.setCourse(course.getCourseJson());
      
      // Set Statistics
      StatisticsEo statisticsEo = new StatisticsEo();

      statisticsEo.setHasNoThumbnail(contentEo.getThumbnail() != null ? 0 : 1);
      statisticsEo.setHasNoDescription(contentEo.getDescription() != null ? 0 : 1);
      statisticsEo.setUsedInCollectionCount((contentEo.getCollectionIds() != null) ? contentEo.getCollectionIds().size() : 0);
      boolean has21CenturySkill = (contentEo.getMetadata() != null && contentEo.getMetadata().containsKey(IndexFields.TWENTY_ONE_CENTURY_SKILL) && !contentEo.getMetadata().getJsonArray(IndexFields.TWENTY_ONE_CENTURY_SKILL).isEmpty()) ? true : false;
      statisticsEo.setHas21stCenturySkills(has21CenturySkill);

      // Set display guide values
      String displayGuideString = source.getString(EntityAttributeConstants.DISPLAY_GUIDE, null);
      JsonObject displayGuide = null;
      if (displayGuideString != null && !displayGuideString.equalsIgnoreCase(IndexerConstants.STR_NULL)) displayGuide = new JsonObject(displayGuideString);
      statisticsEo.setHasFrameBreaker(displayGuide != null ? (Boolean.valueOf(displayGuide.getInteger(EntityAttributeConstants.IS_FRAME_BREAKER).toString())) : false);
      statisticsEo.setStatusIsBroken(displayGuide != null ? displayGuide.getInteger(EntityAttributeConstants.IS_BROKEN) : null);

      // Set display guide
      if(displayGuide != null){
        contentEo.setDisplayGuide(displayGuide);
      }
      
      // Set Editorial tag
      String editorialStr = source.getString(EntityAttributeConstants.EDITORIAL_TAGS, null);
      JsonObject editorialTags = null;
      if (StringUtils.isNotBlank(editorialStr) && !editorialStr.equalsIgnoreCase(IndexerConstants.STR_NULL)) editorialTags = new JsonObject(editorialStr);
      if (editorialTags != null) {
        if (editorialTags.containsKey(EntityAttributeConstants.CONTENT_QUALITY_INDICATOR)
                && editorialTags.getInteger(EntityAttributeConstants.CONTENT_QUALITY_INDICATOR) != null)
          statisticsEo.setContentQualityIndicator(editorialTags.getInteger(EntityAttributeConstants.CONTENT_QUALITY_INDICATOR));
        if (editorialTags.containsKey(EntityAttributeConstants.PUBLISHER_QUALITY_INDICATOR)
                && editorialTags.getInteger(EntityAttributeConstants.PUBLISHER_QUALITY_INDICATOR) != null)
          statisticsEo.setPublisherQualityIndicator(editorialTags.getInteger(EntityAttributeConstants.PUBLISHER_QUALITY_INDICATOR));
      }
      
      long viewsCount = source.getLong(ScoreConstants.VIEW_COUNT);
      statisticsEo.setViewsCount(viewsCount);

      int invalidResource = 0;
      if(StringUtils.trimToNull(contentEo.getTitle()) == null){
        invalidResource = 1;
      }
      statisticsEo.setInvalidResource(invalidResource);

      setCollectionContents(source, contentEo, statisticsEo);

      // Set REEf
      Double efficacy = null;
      Double engagement = null;
      JsonObject signatureResource = getIndexRepo().getSignatureResourcesByContentId(contentEo.getId(), contentEo.getContentFormat());
      if (signatureResource != null) {
        efficacy = (Double) signatureResource.getValue(EntityAttributeConstants.EFFICACY);
        engagement = (Double) signatureResource.getValue(EntityAttributeConstants.ENGAGEMENT);
      }
      statisticsEo.setEfficacy(efficacy);
      statisticsEo.setEngagement(engagement);
      statisticsEo.setRelevance(null);
      
      // Set ranking fields
      Map<String, Object> rankingFields = new HashMap<>();
      rankingFields.put(ScoreConstants.USED_IN_COLLECTION_COUNT, statisticsEo.getUsedInCollectionCount());
      rankingFields.put(ScoreConstants.VIEW_COUNT, viewsCount);
      rankingFields.put(ScoreConstants.HAS_FRAME_BREAKER, statisticsEo.getHasFrameBreaker());
      rankingFields.put(ScoreConstants.HAS_NO_THUMBNAIL, statisticsEo.getHasNoThumbnail());
      rankingFields.put(ScoreConstants.DESCRIPTION_FIELD, contentEo.getDescription());
      rankingFields.put(ScoreConstants.SATS_HAS_NO_DESC, statisticsEo.getHasNoDescription());
      rankingFields.put(ScoreConstants.RESOURCE_URL_FIELD, contentEo.getUrl());
      rankingFields.put(ScoreConstants.HAS_21ST_CENTURY_SKILL, statisticsEo.getHas21stCenturySkills());
      rankingFields.put(ScoreConstants.OER, (contentEo.getInfo() != null && contentEo.getInfo().getInteger(IndexFields.IS_OER, null) != null) ? contentEo.getInfo().getInteger(IndexFields.IS_OER) : 0);
      rankingFields.put(ScoreConstants.PUBLISH_STATUS, contentEo.getPublishStatus());
      rankingFields.put(ScoreConstants.CONTENT_QUALITY_INDICATOR, statisticsEo.getContentQualityIndicator());
      rankingFields.put(ScoreConstants.PUBLISHER_QUALITY_INDICATOR, statisticsEo.getPublisherQualityIndicator());

      JsonObject taxJson = contentEo.getTaxonomy();
      int hasNoStandard = 1;
      if (taxJson != null && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) != null && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) == 1) {
        hasNoStandard = 0;
      }
      rankingFields.put(ScoreConstants.TAX_HAS_NO_STANDARD, hasNoStandard);

      double pcWeight = PCWeightUtil.getResourcePcWeight(new ScoreFields(rankingFields));
      LOGGER.debug("QEISB->build : PC weight : " + pcWeight);
      statisticsEo.setPreComputedWeight(pcWeight);

      contentEo.setStatistics(statisticsEo.getStatistics());

      /*
       * //TODO Add logic to store taxonomy transformation and below details
       * statisticsEo.setHasAdvertisement(hasAdvertisement);
       */

    } catch (Exception e) {
      LOGGER.error("QEISB->build : Failed to build source : Exception", e);
      LOGGER.debug("QEISB -> build : content Eo source : " + contentEo.getContentJson().toString());
      throw new Exception(e);
    }
    return contentEo.getContentJson();
  }

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new ContentEio());
  }

  @Override
  public String getName() {
    return IndexType.RESOURCE.getType();
  }

}
