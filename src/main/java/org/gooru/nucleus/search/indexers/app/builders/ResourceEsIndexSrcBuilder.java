package org.gooru.nucleus.search.indexers.app.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEio;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ResourceEsIndexSrcBuilder<S extends JsonObject, D extends ContentEio> extends ContentEsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new ContentEio());
  }

  @Override
  public String getName() {
    return IndexType.RESOURCE.getType();
  }

  @Override
  protected JsonObject build(JsonObject source, D originalresourceEo) throws Exception {
    try {
      super.build(source, originalresourceEo);

      originalresourceEo.setContentFormat(IndexerConstants.TYPE_RESOURCE);

      // Add Language to info
      String language = source.getString(EntityAttributeConstants.LANGUAGE, null);
      if (language != null && originalresourceEo.getInfo() != null) {
        JsonObject info = originalresourceEo.getInfo();
        info.put(EntityAttributeConstants.LANGUAGE, language);
        originalresourceEo.setInfo(info);
      }

      // Add Audience to metadata
      String metadataString = source.getString(EntityAttributeConstants.METADATA, null);
      JsonObject metadata = new JsonObject(metadataString);
      String audience = source.getString(EntityAttributeConstants.AUDIENCE, null);
      if (audience != null) {
        List<Integer> audienceList = new ArrayList<Integer>();
        for (String audienceNum : audience.substring(1, (audience.length() - 1)).split(IndexerConstants.COMMA)) {
          audienceList.add(Integer.parseInt(audienceNum.trim()));
        }
        JsonArray audienceArray = new JsonArray(audienceList);
        if (audienceArray != null && audienceArray.size() > 0) {
          metadata.put(EntityAttributeConstants.AUDIENCE, audienceArray);
          setMetaData(metadata, originalresourceEo);
        }
      }

      // Set display guide values
      JsonObject displayGuide = null;
      String displayGuideString = source.getString(EntityAttributeConstants.DISPLAY_GUIDE, null);

      // Set frame breaker value
      Integer frameBreaker = (source.getBoolean(EntityAttributeConstants.IS_I_FRAME_BREAKER, false)) ? 1 : 0;
      if (displayGuideString != null) displayGuide = new JsonObject(displayGuideString);
      displayGuide.put(EntityAttributeConstants.IS_FRAME_BREAKER, frameBreaker);

      // Set is Broken value
      Integer isBroken = (source.getBoolean(EntityAttributeConstants.IS_BROKEN, false)) ? 1 : 0;
      displayGuide.put(EntityAttributeConstants.IS_BROKEN, isBroken);

      if (displayGuide != null) {
        originalresourceEo.setDisplayGuide(displayGuide);
      }

      // Set Statistics
      StatisticsEo statisticsEo = new StatisticsEo();
      statisticsEo.setHasFrameBreaker(frameBreaker);
      statisticsEo.setStatusIsBroken(isBroken);
      statisticsEo.setHasNoThumbnail(originalresourceEo.getThumbnail() != null ? 0 : 1);
      statisticsEo.setHasNoDescription(originalresourceEo.getDescription() != null ? 0 : 1);
      statisticsEo.setUsedInCollectionCount((originalresourceEo.getCollectionIds() != null) ? originalresourceEo.getCollectionIds().size() : 0);
      boolean has21CenturySkill = (originalresourceEo.getMetadata() != null && originalresourceEo.getMetadata().containsKey(IndexerConstants.TWENTY_ONE_CENTURY_SKILL) && !originalresourceEo.getMetadata().getJsonArray(IndexerConstants.TWENTY_ONE_CENTURY_SKILL).isEmpty()) ? true : false;
      statisticsEo.setHas21stCenturySkills(has21CenturySkill);

      // Set Editorial tag
      String editorialStr = source.getString(EntityAttributeConstants.EDITORIAL_TAGS, null);
      JsonObject editorialTags = null;
      if (editorialStr != null && !editorialStr.isEmpty()) editorialTags = new JsonObject(editorialStr);
      statisticsEo.setContentQualityIndicator(editorialTags != null ? editorialTags.getInteger(EntityAttributeConstants.CONTENT_QUALITY_INDICATOR) : null);
      statisticsEo.setPublisherQualityIndicator(editorialTags != null ? editorialTags.getInteger(EntityAttributeConstants.PUBLISHER_QUALITY_INDICATOR) : null);

      long viewsCount = source.getLong(ScoreConstants.VIEW_COUNT);
      statisticsEo.setViewsCount(viewsCount);

      int invalidResource = 0;
      if (StringUtils.trimToNull(originalresourceEo.getTitle()) == null) {
        invalidResource = 1;
      }
      statisticsEo.setInvalidResource(invalidResource);

      // Set license
      Integer licenseId = source.getInteger(EntityAttributeConstants.LICENSE);
      JsonObject license = getLicenseData(licenseId);
      if (license != null) {
        originalresourceEo.setLicense(license);
      }

      originalresourceEo.setStatistics(statisticsEo.getStatistics());
      // Set ranking fields
      Map<String, Object> rankingFields = new HashMap<>();
      rankingFields.put(ScoreConstants.USED_IN_COLLECTION_COUNT, statisticsEo.getUsedInCollectionCount());
      rankingFields.put(ScoreConstants.VIEW_COUNT, viewsCount);
      rankingFields.put(ScoreConstants.HAS_FRAME_BREAKER, statisticsEo.getHasFrameBreaker());
      rankingFields.put(ScoreConstants.HAS_NO_THUMBNAIL, statisticsEo.getHasNoThumbnail());
      rankingFields.put(ScoreConstants.DESCRIPTION_FIELD, originalresourceEo.getDescription());
      rankingFields.put(ScoreConstants.SATS_HAS_NO_DESC, statisticsEo.getHasNoDescription());
      rankingFields.put(ScoreConstants.RESOURCE_URL_FIELD, originalresourceEo.getUrl());
      rankingFields.put(ScoreConstants.HAS_21ST_CENTURY_SKILL, statisticsEo.getHas21stCenturySkills());
      rankingFields.put(ScoreConstants.OER, (originalresourceEo.getInfo() != null && originalresourceEo.getInfo().getInteger(IndexFields.IS_OER, null) != null) ? originalresourceEo.getInfo().getInteger(IndexFields.IS_OER) : 0);
      rankingFields.put(ScoreConstants.PUBLISH_STATUS, originalresourceEo.getPublishStatus());
      rankingFields.put(ScoreConstants.CONTENT_QUALITY_INDICATOR, statisticsEo.getContentQualityIndicator());
      rankingFields.put(ScoreConstants.PUBLISHER_QUALITY_INDICATOR, statisticsEo.getPublisherQualityIndicator());

      JsonObject taxJson = originalresourceEo.getTaxonomy();
      int hasNoStandard = 1;
      if (taxJson != null && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) != null
              && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) == 1) {
        hasNoStandard = 0;
      }
      rankingFields.put(ScoreConstants.TAX_HAS_NO_STANDARD, hasNoStandard);

      double pcWeight = PCWeightUtil.getResourcePcWeight(new ScoreFields(rankingFields));
      LOGGER.debug("REISB->build : PC weight : " + pcWeight);
      statisticsEo.setPreComputedWeight(pcWeight);

      originalresourceEo.setStatistics(statisticsEo.getStatistics());
    } catch (Exception e) {
      LOGGER.error("REISB->build : Failed to build source : Exception", e);
      LOGGER.debug("REISB -> build : originalresourceEo Eo source : " + originalresourceEo.getContentJson().toString());
      throw new Exception(e);
    }
    return originalresourceEo.getContentJson();
  }

}
