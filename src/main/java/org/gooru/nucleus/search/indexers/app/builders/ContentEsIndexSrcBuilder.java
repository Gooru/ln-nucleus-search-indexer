package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEio;
import org.gooru.nucleus.search.indexers.app.index.model.ResourceInfoEo;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;

import com.google.common.base.CaseFormat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ContentEsIndexSrcBuilder<S extends JsonObject, D extends ContentEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new ContentEio());
  }

  @Override
  public String getName() {
    return IndexType.RESOURCE.getType();
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected JsonObject build(JsonObject source, D contentEo) throws Exception {
    try {
      String id = source.getString(EntityAttributeConstants.ID);
      contentEo.setId(id);
      contentEo.setIndexId(id);
      contentEo.setIndexType(getName());
      contentEo.setUrl(source.getString(EntityAttributeConstants.URL, null));
      contentEo.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
      String description = source.getString(EntityAttributeConstants.DESCRIPTION, null);
      contentEo.setDescription(description);
      contentEo.setIndexUpdatedTime(new Date(System.currentTimeMillis()));
      contentEo.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      contentEo.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT));
      contentEo.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
      contentEo.setPublishStatus(source.getString(EntityAttributeConstants.PUBLISH_STATUS, null));
      contentEo.setNarration(source.getString(EntityAttributeConstants.NARRATION, null));
      String thumbnail = source.getString(EntityAttributeConstants.THUMBNAIL, null);
      contentEo.setThumbnail(thumbnail);
      contentEo.setIsCopyrightOwner(source.getBoolean(EntityAttributeConstants.IS_COPYRIGHT_OWNER, null));
      contentEo.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        JsonObject creator = getUserRepo().getUser(creatorId);
        if (creator != null && !creator.isEmpty()) {
          setUser(creator, creatorEo);
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
      
      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      JsonObject taxonomyObject = null;
      TaxonomyEo taxonomyEo = new TaxonomyEo();
      try {
        if (StringUtils.isNotBlank(taxonomy) && !taxonomy.equalsIgnoreCase(IndexerConstants.STR_NULL)) taxonomyObject = new JsonObject(taxonomy);
        addTaxonomy(taxonomyObject, taxonomyEo);
      } catch (Exception e) {
        LOGGER.error("Unable to convert Taxonomy to JsonObject", e);
      }
      contentEo.setTaxonomy(taxonomyEo.getTaxonomyJson());

      // Set info
      String infoStr = source.getString(EntityAttributeConstants.INFO);
      if (StringUtils.isNotBlank(infoStr) && !infoStr.equalsIgnoreCase(IndexerConstants.STR_NULL)) {
        JsonObject info = new JsonObject(infoStr);
        JsonObject infoEo = new JsonObject();
        if (info.containsKey(EntityAttributeConstants.CONTRIBUTOR) && info.getJsonArray(EntityAttributeConstants.CONTRIBUTOR) != null && info.getJsonArray(EntityAttributeConstants.CONTRIBUTOR).size() > 0) {
          infoEo.put(EntityAttributeConstants.CONTRIBUTOR_ANALYZED, info.getJsonArray(EntityAttributeConstants.CONTRIBUTOR));
        }
        if (info.containsKey(EntityAttributeConstants.CRAWLED_SUB) && StringUtils.isNotBlank(info.getString(EntityAttributeConstants.CRAWLED_SUB))) {
          infoEo.put(EntityAttributeConstants.CRAWLED_SUB_ANALYZED, info.getString(EntityAttributeConstants.CRAWLED_SUB));
        }

        // Change underscore fields names to camel case
        for (String fieldName : info.fieldNames()) {
          if (info.getValue(fieldName) != null) {
            infoEo.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName), info.getValue(fieldName));
          }
        }
        contentEo.setInfo(infoEo);

      }  
      
      // Set license
      Integer licenseId = source.getInteger(EntityAttributeConstants.LICENSE);
      JsonObject license = getLicenseData(licenseId);
      if(license != null){
        contentEo.setLicense(license);
      }

      //Set Extracted Text
      ResourceInfoEo resourceInfoJson = new ResourceInfoEo();
      String extractedText = source.getString(IndexerConstants.TEXT);
      if (StringUtils.isNotBlank(extractedText)) {
        resourceInfoJson.setText(extractedText);
      }
      JsonObject watsonTags = source.getJsonObject(IndexerConstants.WATSON_TAGS);
      if (watsonTags != null && !watsonTags.isEmpty()) {
        resourceInfoJson.setWatsonTags(watsonTags);
      }
      if(!resourceInfoJson.getResourceInfo().isEmpty()) contentEo.setResourceInfo(resourceInfoJson.getResourceInfo());

      //Set Content Tenant
      String tenantId = source.getString(EntityAttributeConstants.TENANT);
      String tenantRoot = source.getString(EntityAttributeConstants.TENANT_ROOT);
      JsonObject tenant = new JsonObject();
      tenant.put(IndexerConstants.TENANT_ID, tenantId);
      tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
      contentEo.setTenant(tenant);
      
      /*
       * //TODO Add logic to store below details
       * statisticsEo.setHasAdvertisement(hasAdvertisement);
       */

    } catch (Exception e) {
      LOGGER.error("CEISB->build : Failed to build source : Exception", e);
      LOGGER.debug("CEISB -> build : content Eo source : " + contentEo.getContentJson().toString());
      throw new Exception(e);
    }
    return contentEo.getContentJson();

  }

  @SuppressWarnings("rawtypes")
  protected void setCollectionContents(JsonObject source, D contentEo, StatisticsEo statisticsEo) {
    // Set Collection info of content
    JsonArray collectionIds = new JsonArray();
    JsonArray collectionTitles = new JsonArray();
    String collectionId = source.getString(EntityAttributeConstants.COLLECTION_ID, null);
    String collectionTitle = source.getString(IndexerConstants.COLLECTION_TITLE, null);
    String collectionFormat = source.getString(EntityAttributeConstants.FORMAT, null);
    if (collectionId != null) {
      collectionIds.add(collectionId);
    }
    if (collectionTitle != null) {
      collectionTitles.add(collectionTitle);
    }
    List<Map> collectionMetaAsList = getContentRepo().getCollectionMeta(contentEo.getId());

    if (collectionMetaAsList != null && collectionMetaAsList.size() > 0) {
      for (Map collectionMetaMap : collectionMetaAsList) {
        String usedCollectionId = collectionMetaMap.get(EntityAttributeConstants.ID).toString();
        String format = collectionMetaMap.get(EntityAttributeConstants.FORMAT).toString();
        collectionIds.add(usedCollectionId);
        collectionTitles.add(collectionMetaMap.get(EntityAttributeConstants.TITLE));
        classifyCollections(format, statisticsEo);
      }
      classifyCollections(collectionFormat, statisticsEo);
    }
    if (!collectionIds.isEmpty()) contentEo.setCollectionIds(collectionIds);
    if (!collectionTitles.isEmpty()) contentEo.setCollectionTitles(new JsonArray(collectionTitles.stream().distinct().collect(Collectors.toList())));
  }
  
  private void classifyCollections(String format, StatisticsEo statisticsEo) {
    if (format != null) {
      switch (format) {
      case IndexerConstants.COLLECTION:
        statisticsEo.setCollectionCount(statisticsEo.getCollectionCount() + 1);
        break;
      case IndexerConstants.ASSESSMENT:
        statisticsEo.setAssessmentCount(statisticsEo.getAssessmentCount() + 1);
        break;
      case IndexerConstants.ASSESSMENT_EXTERNAL:
        statisticsEo.setExternalAssessmentCount(statisticsEo.getExternalAssessmentCount() + 1);
        break;
      }
    }
  }

  protected void setMetaData(JsonObject metaData, ContentEio contentEo) {
    if (metaData != null) {
      JsonObject dataMap = new JsonObject();
      for (String fieldName : metaData.fieldNames()) {
        // Temp logic to only process array fields
        Object metaValue = metaData.getValue(fieldName);
        if (metaValue instanceof JsonArray) {
          JsonArray value = extractMetaValues(metaData, fieldName);
          String key = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
          if (value != null && !value.isEmpty())
            dataMap.put(key, value);
          if (dataMap != null && !dataMap.isEmpty())
            contentEo.setMetadata(dataMap);
        }
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private JsonArray extractMetaValues(JsonObject metadata, String fieldName){
    JsonArray value = new JsonArray();
    JsonArray references = metadata.getJsonArray(fieldName);
    if (references != null && references.size() > 0) {
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
