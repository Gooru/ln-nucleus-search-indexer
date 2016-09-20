package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.AnswerEo;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEio;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.HintEo;
import org.gooru.nucleus.search.indexers.app.index.model.QuestionEo;
import org.gooru.nucleus.search.indexers.app.index.model.ResourceInfoEo;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;

import com.google.common.base.CaseFormat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 */
public class ContentEsIndexSrcBuilder<S extends JsonObject, D extends ContentEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("rawtypes")
  @Override
  public JsonObject build(JsonObject source, D contentEo) throws Exception {
    try {
      String id = source.getString(EntityAttributeConstants.ID);
      contentEo.setId(id);
      contentEo.setIndexId(id);
      contentEo.setIndexType(getName());
      String contentFormat = source.getString(EntityAttributeConstants.CONTENT_FORMAT, null);
      contentEo.setContentFormat(contentFormat);
      contentEo.setUrl(source.getString(EntityAttributeConstants.URL, null));
      contentEo.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
      String description = source.getString(EntityAttributeConstants.DESCRIPTION, null);
      contentEo.setDescription(description);
      contentEo.setIndexUpdatedTime(new Date(System.currentTimeMillis()));
      contentEo.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      contentEo.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT));
      contentEo.setOriginalContentId(source.getString(EntityAttributeConstants.ORIGINAL_CONTENT_ID, null));
      contentEo.setParentContentId(source.getString(EntityAttributeConstants.PARENT_CONTENT_ID, null));
      contentEo.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
      contentEo.setPublishStatus(source.getString(EntityAttributeConstants.PUBLISH_STATUS, null));
      contentEo.setNarration(source.getString(EntityAttributeConstants.NARRATION, null));
      String thumbnail = source.getString(EntityAttributeConstants.THUMBNAIL, null);
      contentEo.setThumbnail(thumbnail);
      contentEo.setCollectionId(source.getString(EntityAttributeConstants.COLLECTION_ID, null));
      contentEo.setIsCopyrightOwner(source.getBoolean(EntityAttributeConstants.IS_COPYRIGHT_OWNER, null));
      contentEo.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));

      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo orginalCreatorEo = new UserEo();
        List<Map> orginalCreator = getUserRepo().getUserDetails(originalCreatorId);
        if (orginalCreator != null && orginalCreator.size() > 0) {
          setUser(orginalCreator.get(0), orginalCreatorEo);
          contentEo.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        List<Map> creator = getUserRepo().getUserDetails(creatorId);
        if (creator != null && creator.size() > 0) {
          setUser(creator.get(0), creatorEo);
          contentEo.setCreator(creatorEo.getUser());
        }
      }
      // Set ContentSubFormat type escaped
      String contentSubFormat = source.getString(EntityAttributeConstants.CONTENT_SUB_FORMAT, null);
      contentEo.setContentSubFormat(contentSubFormat);
      String contentSubTypeEscaped = null;
      if (contentSubFormat.contains("-")) {
        contentSubTypeEscaped = contentSubFormat.replace("-", "");
      } else if (contentSubFormat.contains("_")) {
        contentSubTypeEscaped = contentSubFormat.replace("_", "");
      } else {
        contentSubTypeEscaped = contentSubFormat.replace("/", "");
      }
      contentEo.setContentSubFormatEscaped(contentSubTypeEscaped);
      // Set CopyrightOwner
      String copyrightOwner = source.getString(EntityAttributeConstants.COPYRIGHT_OWNER, null);
      if (copyrightOwner != null && !copyrightOwner.equalsIgnoreCase(IndexerConstants.EMPTY_ARRAY)) {
        JsonArray copyrightOwnerJsonArray = new JsonArray(copyrightOwner);
        if (copyrightOwnerJsonArray != null) {
          contentEo.setCopyrightOwnerList(copyrightOwnerJsonArray);
        }
      }
      // Set Question
      QuestionEo questionEo = new QuestionEo();
       
      if(contentFormat != null && contentFormat.equalsIgnoreCase(IndexerConstants.TYPE_QUESTION)){
        questionEo.setQuestionText(contentEo.getDescription());
      }
      
      String answerJson = source.getString(EntityAttributeConstants.ANSWER, null);
      if (answerJson != null) {
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
      if (hintDetail != null) {
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
      // Set Metadata
      String metadataString = source.getString(EntityAttributeConstants.METADATA, null);
      setMetaData(metadataString, contentEo);

      // Set Collection info of content
      JsonArray collectionIds = new JsonArray();
      JsonArray collectionTitles = new JsonArray();
      String collectionId = source.getString(EntityAttributeConstants.COLLECTION_ID, null);
      String collectionTitle = source.getString(IndexerConstants.COLLECTION_TITLE, null);
      if (collectionId != null) {
        collectionIds.add(collectionId);
      }
      if(collectionTitle != null){
        collectionTitles.add(collectionTitle);
      }
      List<Map> collectionMetaAsList = getContentRepo().getCollectionMeta(id);
      if (collectionMetaAsList != null && collectionMetaAsList.size() > 0) {
        for (Map collectionMetaMap : collectionMetaAsList) {
          String usedCollectionId = collectionMetaMap.get(EntityAttributeConstants.ID).toString();
          collectionIds.add(usedCollectionId);
          collectionTitles.add(collectionMetaMap.get(EntityAttributeConstants.TITLE));
        }
      }
      if (!collectionIds.isEmpty()) contentEo.setCollectionIds(collectionIds);
      if (!collectionTitles.isEmpty()) contentEo.setCollectionTitles(new JsonArray(collectionTitles.stream().distinct().collect(Collectors.toList())));

      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      JsonObject taxonomyObject = null;
      TaxonomyEo taxonomyEo = new TaxonomyEo();
      try {
        if (taxonomy != null) taxonomyObject = new JsonObject(taxonomy);
        addTaxonomy(taxonomyObject, taxonomyEo);
      } catch (Exception e) {
        LOGGER.error("Unable to convert Taxonomy to JsonObject", e);
      }
      contentEo.setTaxonomy(taxonomyEo.getTaxonomyJson());

      // Set info
      String infoStr = source.getString(EntityAttributeConstants.INFO);
      int oer = 0;
      if(infoStr != null){
        JsonObject info = new JsonObject(infoStr);
        JsonObject infoEo = new JsonObject();
        if(info.getInteger(EntityAttributeConstants.OER) != null){
         oer = info.getInteger(EntityAttributeConstants.OER);
        }

        if(info.getJsonArray(EntityAttributeConstants.CONTRIBUTOR) != null && info.getJsonArray(EntityAttributeConstants.CONTRIBUTOR).size() > 0){
          infoEo.put(EntityAttributeConstants.CONTRIBUTOR_ANALYZED, info.getJsonArray(EntityAttributeConstants.CONTRIBUTOR));
        }
        if(info.getString(EntityAttributeConstants.CRAWLED_SUB) != null && !info.getString(EntityAttributeConstants.CRAWLED_SUB).isEmpty()){
          infoEo.put(EntityAttributeConstants.CRAWLED_SUB_ANALYZED, info.getString(EntityAttributeConstants.CRAWLED_SUB));
        }

        // Change underscore fields names to camel case
        for(String fieldName : info.fieldNames()){
          if(info.getValue(fieldName) != null){
            infoEo.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName), info.getValue(fieldName));
          }
        }
        contentEo.setInfo(infoEo);
      }

      // Set Statistics
      StatisticsEo statisticsEo = new StatisticsEo();
      statisticsEo.setHasNoThumbnail(thumbnail != null ? 0 : 1);
      statisticsEo.setHasNoDescription(description != null ? 0 : 1);
      statisticsEo.setUsedInCollectionCount(collectionIds.size());
      boolean has21CenturySkill = (contentEo.getMetadata().containsKey(IndexerConstants.TWENTY_ONE_CENTURY_SKILL) && !contentEo.getMetadata().getJsonArray("twentyOneCenturySkill").isEmpty()) ? true : false;
      statisticsEo.setHas21stCenturySkills(has21CenturySkill);
      // Set display guide values
      String displayGuideString = source.getString(EntityAttributeConstants.DISPLAY_GUIDE, null);
      JsonObject displayGuide = null;
      if (displayGuideString != null) displayGuide = new JsonObject(displayGuideString);
      statisticsEo.setHasFrameBreaker(displayGuide != null ? displayGuide.getInteger(EntityAttributeConstants.IS_FRAME_BREAKER) : null);
      statisticsEo.setStatusIsBroken(displayGuide != null ? displayGuide.getInteger(EntityAttributeConstants.IS_BROKEN) : null);

      // Set display guide
      if(displayGuide != null){
        contentEo.setDisplayGuide(displayGuide);
      }
      
      //Set CUL course mapped
      CourseEo course = new CourseEo(); 
      course.setId(source.getString(IndexerConstants.RESOURCE_COURSE_ID, null));
      course.setTitle(source.getString(IndexerConstants.RESOURCE_COURSE, null));
      contentEo.setCourse(course.getCourseJson());
      
      //Set Editorial tag
      String editorialStr = source.getString(EntityAttributeConstants.EDITORIAL_TAGS, null);
      JsonObject editorialTags = null; 
      if (editorialStr != null && !editorialStr.isEmpty()) editorialTags = new JsonObject(editorialStr);
      statisticsEo.setContentQualityIndicator(editorialTags != null ? editorialTags.getInteger(EntityAttributeConstants.CONTENT_QUALITY_INDICATOR) : 0);
      statisticsEo.setPublisherQualityIndicator(editorialTags != null ? editorialTags.getInteger(EntityAttributeConstants.PUBLISHER_QUALITY_INDICATOR) : 0);
      
      long viewsCount = source.getLong(ScoreConstants.VIEW_COUNT);
      statisticsEo.setViewsCount(viewsCount);

      int invalidResource = 0;
      if(StringUtils.trimToNull(contentEo.getTitle()) == null){
        invalidResource = 1;
      }
      statisticsEo.setInvalidResource(invalidResource);
      
      // Set license
      Integer licenseId = source.getInteger(EntityAttributeConstants.LICENSE);
      JsonObject license = getLicenseData(licenseId);
      if(license != null){
        contentEo.setLicense(license);
      }

      // Set ranking fields
      Map<String, Object> rankingFields = new HashMap<>();
      rankingFields.put(ScoreConstants.USED_IN_COLLECTION_COUNT, statisticsEo.getUsedInCollectionCount());
      rankingFields.put(ScoreConstants.VIEW_COUNT, viewsCount);
      rankingFields.put(ScoreConstants.HAS_FRAME_BREAKER, statisticsEo.getHasFrameBreaker());
      rankingFields.put(ScoreConstants.HAS_NO_THUMBNAIL, statisticsEo.getHasNoThumbnail());
      rankingFields.put(ScoreConstants.DESCRIPTION_FIELD, description);
      rankingFields.put(ScoreConstants.SATS_HAS_NO_DESC, statisticsEo.getHasNoDescription());
      rankingFields.put(ScoreConstants.RESOURCE_URL_FIELD, contentEo.getUrl());
      rankingFields.put(ScoreConstants.HAS_21ST_CENTURY_SKILL, statisticsEo.getHas21stCenturySkills());
      rankingFields.put(ScoreConstants.OER, oer);
      rankingFields.put(ScoreConstants.PUBLISH_STATUS, contentEo.getPublishStatus());

      JsonObject taxJson = contentEo.getTaxonomy();
      
      int hasNoStandard = 1;
      
      if (taxJson != null && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) != null && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) == 1) {
        hasNoStandard = 0;
      }

      rankingFields.put(ScoreConstants.TAX_HAS_NO_STANDARD, hasNoStandard);

      double pcWeight = PCWeightUtil.getResourcePcWeight(new ScoreFields(rankingFields));
      LOGGER.debug("CEISB->build : PC weight : " + pcWeight);
      statisticsEo.setPreComputedWeight(pcWeight);

      contentEo.setStatistics(statisticsEo.getStatistics());

      //Set Extracted Text
      String extractedText = source.getString(IndexerConstants.TEXT);
      if (extractedText != null) {
        ResourceInfoEo resourceInfoJson = new ResourceInfoEo();
        resourceInfoJson.setText(extractedText);
        contentEo.setResourceInfo(resourceInfoJson.getResourceInfo());
      }

      /*
       * //TODO Add logic to store taxonomy transformation and below details
       * statisticsEo.setHasAdvertisement(hasAdvertisement);
       * statisticsEo.setHas21stCenturySkills(has21stCenturySkills);
       */

    } catch (Exception e) {
      LOGGER.error("CEISB->build : Failed to build source : Exception", e);
      LOGGER.debug("CEISB -> build : content Eo source : " + contentEo.getContentJson().toString());
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

  private void setMetaData(String jsonString, ContentEio contentEo){
    if (jsonString != null) {
      JsonObject data = new JsonObject(jsonString);
      if (data != null) {
        JsonObject dataMap = new JsonObject();
        for (String fieldName : data.fieldNames()) {
          // Temp logic to only process array fields
          Object metaValue = data.getValue(fieldName);
          if (metaValue instanceof JsonArray) {
            JsonArray value = extractMetaValues(data, fieldName);
            String key = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
            if (value != null && !value.isEmpty())
              dataMap.put(key, value);
            if (dataMap != null && !dataMap.isEmpty())
              contentEo.setMetadata(dataMap);
          }
        }
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private JsonArray extractMetaValues(JsonObject metadata, String fieldName){
    JsonArray value = new JsonArray();
    JsonArray references = metadata.getJsonArray(fieldName);
    if (references != null) {
      String referenceIds = references.toString();
      List<Map> metacontent = null;
      if (fieldName.equalsIgnoreCase(EntityAttributeConstants.TWENTY_ONE_CENTURY_SKILL)) {
        metacontent = getIndexRepo().getTwentyOneCenturySkill(referenceIds.substring(1, referenceIds.length() - 1));
      } else {
        metacontent = getIndexRepo().getMetadata(referenceIds.substring(1, referenceIds.length() - 1));
      }
      if (metacontent != null) {
        for (Map metaMap : metacontent) {
          value.add(metaMap.get(EntityAttributeConstants.LABEL).toString());
        }
      }
    }
    return value;
  }

}
