package org.gooru.nucleus.search.indexers.app.builders;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEio;
import org.gooru.nucleus.search.indexers.app.index.model.ResourceInfoEo;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;

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
          try {
            //Extract and Index Publishers
            extractAndIndexPublishers(copyrightOwnerJsonArray.stream().distinct().map(e -> e.toString()).collect(Collectors.toList()));
          } catch (Exception e) {
            LOGGER.debug("Error while extracting publishers : {}" , e);
          }
        }
      }

      Set<String> gooruSubjectCodes = null;
      Set<String> gooruCourseCodes = null;
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
        if (info.containsKey(EntityAttributeConstants.GOORU_SUBJECT)) {
          JsonArray gooruSubs = info.getJsonArray(EntityAttributeConstants.GOORU_SUBJECT);
          gooruSubjectCodes = new HashSet<>();
          for (Object o : gooruSubs) {
            String subjectTitle = (String) o;
            String subjectCode = getTaxonomyRepo().getGutSubjectCodeByTitle(subjectTitle);
            if (StringUtils.isNotBlank(subjectCode)) {
              gooruSubjectCodes.add(subjectCode);
            }
          }
        }
        if (info.containsKey(EntityAttributeConstants.GOORU_COURSE)) {
          JsonArray gooruCourses = info.getJsonArray(EntityAttributeConstants.GOORU_COURSE);
          gooruCourseCodes = new HashSet<>();
          for (Object o : gooruCourses) {
            String courseTitle = (String) o;
            String courseCode = getIndexRepo().getCurrentCourseCodeByOldTitle(courseTitle);
            if (StringUtils.isBlank(courseCode)) courseCode = getTaxonomyRepo().getCourseCodeByTitleAndFw(courseTitle, IndexerConstants.GUT_FRAMEWORK);
            if (StringUtils.isNotBlank(courseCode)) {
              gooruCourseCodes.add(courseCode);
            }
          }
        }
        // Change underscore fields names to camel case
        for (String fieldName : info.fieldNames()) {
          if (info.getValue(fieldName) != null) {
            infoEo.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName), info.getValue(fieldName));
          }
        }
        contentEo.setInfo(infoEo);

      }  
      
      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      String aggTaxonomy = source.getString(EntityAttributeConstants.AGGREGATED_TAXONOMY, null);
      String aggGutCodes = source.getString(EntityAttributeConstants.AGGREGATED_GUT_CODES, null);
      String subject = source.getString(EntityAttributeConstants.SUBJECT, null);
      JsonObject taxonomyObject = null;
      JsonObject aggTaxonomyObject = null;
      JsonObject aggGutCodesObject = null;
      JsonObject gooruSubjectObject = null;
      TaxonomyEo taxonomyEo = new TaxonomyEo();
      try {
        if (StringUtils.isNotBlank(taxonomy) && !taxonomy.equalsIgnoreCase(IndexerConstants.STR_NULL)) taxonomyObject = new JsonObject(taxonomy);
        if (StringUtils.isNotBlank(aggTaxonomy) && !aggTaxonomy.equalsIgnoreCase(IndexerConstants.STR_NULL)) aggTaxonomyObject = new JsonObject(aggTaxonomy);
        if (StringUtils.isNotBlank(aggGutCodes) && !aggGutCodes.equalsIgnoreCase(IndexerConstants.STR_NULL)) aggGutCodesObject = new JsonObject(aggGutCodes);
        if (StringUtils.isNotBlank(subject) && !subject.equalsIgnoreCase(IndexerConstants.STR_NULL)) gooruSubjectObject = new JsonObject(subject);
        
        if (gooruSubjectCodes != null && !gooruSubjectCodes.isEmpty()) {
          if (aggGutCodesObject == null) aggGutCodesObject = new JsonObject();
          for (String gooruSubjectCode : gooruSubjectCodes) {
            aggGutCodesObject.put(gooruSubjectCode, new JsonObject());
          }
        }
        if (gooruCourseCodes != null && !gooruCourseCodes.isEmpty()) {
          if (aggGutCodesObject == null) aggGutCodesObject = new JsonObject();
          for (String gooruCourseCode : gooruCourseCodes) {
            aggGutCodesObject.put(gooruCourseCode, new JsonObject());
          }
        }
        if (gooruSubjectObject != null) {
          if (aggGutCodesObject == null) aggGutCodesObject = new JsonObject();
          for (String key : gooruSubjectObject.fieldNames()) {
            aggGutCodesObject.put(key, gooruSubjectObject.getValue(key));
          }
        }
        addTaxonomy(taxonomyObject, taxonomyEo, aggTaxonomyObject, aggGutCodesObject);
      } catch (Exception e) {
        LOGGER.error("Unable to convert Taxonomy to JsonObject", e);
      }
      contentEo.setTaxonomy(taxonomyEo.getTaxonomyJson());

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
  
  private void extractAndIndexPublishers(List<String> copyrightOwners) {
    BulkRequest bulkRequest = new BulkRequest();
    Set<String> publishers = new HashSet<>();
    for (String copyrightOwner : copyrightOwners) {
      String copyrightOwnerString = copyrightOwner.trim();
      if (copyrightOwnerString.length() > 0 && !publishers.contains(copyrightOwnerString.toLowerCase())) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        QueryBuilder filter = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(IndexFields.PUBLISHER_DOT_PUBLISHER_LOWERCASE, copyrightOwnerString.toLowerCase()));
        sourceBuilder.query(filter);
        SearchResponse result = null;
        try {
          result = IndexService.instance().getDocument(IndexNameHolder.getIndexName(EsIndex.CONTENT_PROVIDER), IndexType.PUBLISHER.getType(), sourceBuilder);
        } catch (Exception e) {
          LOGGER.debug("Error while searching publisher" , copyrightOwnerString);
        }
        if (result != null && result.getHits() != null && result.getHits().getHits().length > 0) {
          LOGGER.debug("Publisher is already available in index : {}" , copyrightOwnerString);
          continue;
        }
        publishers.add(copyrightOwnerString.toLowerCase());
        String id = UUID.randomUUID().toString();
        JsonObject data = new JsonObject().put(EntityAttributeConstants.ID, id).put(IndexerConstants.PUBLISHER, copyrightOwnerString).put(
                IndexFields.PUBLISHER_SUGGEST, copyrightOwnerString.replaceAll(IndexerConstants.REGEXP_NON_WORDS, IndexerConstants.EMPTY_STRING));
        IndexRequest request = new IndexRequest(IndexNameHolder.getIndexName(EsIndex.CONTENT_PROVIDER), IndexType.PUBLISHER.getType(), id).source(data.toString(), XContentType.JSON); 
        bulkRequest.add(request);
      }
    }
    if (bulkRequest.numberOfActions() > 0) {
      bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
      BulkResponse bulkResponse;
      try {
        bulkResponse = getClient().bulk(bulkRequest);
        if (bulkResponse.hasFailures()) {
          BulkItemResponse[] responses = bulkResponse.getItems();
          for (BulkItemResponse response : responses) {
            if (response.isFailed()) {
              INDEX_FAILURES_LOGGER.error("Failed Bulk index for publisher : " + response.getId() + " Exception " + response.getFailureMessage());
            }
          }
        } else {
          LOGGER.debug("Successfully indexed bulk publishers!");
        }
      } catch (IOException e) {
        INDEX_FAILURES_LOGGER.error("Failed Bulk index for publisher! Exception " + e.getMessage());
      }
    }
  }
  
}
