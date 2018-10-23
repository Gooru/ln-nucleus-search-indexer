package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CollectionContentEo;
import org.gooru.nucleus.search.indexers.app.index.model.CollectionEio;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.SignatureItemsRepository;
import org.gooru.nucleus.search.indexers.app.utils.BaseUtil;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 */
public class CollectionEsIndexSrcBuilder<S extends JsonObject, D extends CollectionEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("rawtypes")
  @Override
  public JsonObject build(JsonObject source, D collectionEo) throws Exception {
    try {
      //LOGGER.debug("CEISB->build : index source : " + source.toString());
      String id = source.getString(EntityAttributeConstants.ID);
      collectionEo.setId(source.getString(EntityAttributeConstants.ID));
      collectionEo.setIndexId(id);
      collectionEo.setIndexType(getName());
      collectionEo.setUrl(source.getString(EntityAttributeConstants.URL, null));
      collectionEo.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
      collectionEo.setContentFormat(source.getString(EntityAttributeConstants.FORMAT, null));
      collectionEo.setIndexUpdatedTime(new Date());
      collectionEo.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      collectionEo.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT, null));
      collectionEo.setOriginalCollectionId(source.getString(EntityAttributeConstants.ORIGINAL_COLLECTION_ID, null));
      collectionEo.setParentCollectionId(source.getString(EntityAttributeConstants.PARENT_COLLECTION_ID, null));
      collectionEo.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
      collectionEo.setPublishStatus(source.getString(EntityAttributeConstants.PUBLISH_STATUS, null));
      collectionEo.setContentFormat(source.getString(EntityAttributeConstants.CONTENT_FORMAT, null));
      collectionEo.setContentSubFormat(source.getString(EntityAttributeConstants.SUB_FORMAT, null));
      String thumbnail = source.getString(EntityAttributeConstants.THUMBNAIL, null);
      collectionEo.setThumbnail(thumbnail);
      String learningObjective = source.getString(EntityAttributeConstants.LEARNING_OBJECTIVE, null);
      collectionEo.setLearningObjective(learningObjective);
      collectionEo.setOrientation(source.getString(EntityAttributeConstants.ORIENTATION, null));
      collectionEo.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));
      collectionEo.setGradingType(source.getString(EntityAttributeConstants.GRADING, null));
      collectionEo.setModifierId(source.getString(EntityAttributeConstants.MODIFIER_ID, null));
      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo orginalCreatorEo = new UserEo();
        JsonObject orginalCreator = getUserRepo().getUser(originalCreatorId);
        if (orginalCreator != null && !orginalCreator.isEmpty()) {
          setUser(orginalCreator, orginalCreatorEo);
          collectionEo.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        JsonObject creator = getUserRepo().getUser(creatorId);
        if (creator != null && !creator.isEmpty()) {
          setUser(creator, creatorEo);
          collectionEo.setCreator(creatorEo.getUser());
        }
      }
      // Set Owner
      String ownerId = source.getString(EntityAttributeConstants.OWNER_ID, null);
      if (ownerId != null) {
        UserEo ownerEo = new UserEo();
        JsonObject owner = getUserRepo().getUser(ownerId);
        if (owner != null && !owner.isEmpty()) {
          setUser(owner, ownerEo);
          collectionEo.setOwner(ownerEo.getUser());
        }
      }
      
      // Set Metadata
      String metadataString = source.getString(EntityAttributeConstants.METADATA, null);
      JsonObject metadata = null;
      if (StringUtils.isNotBlank(metadataString) && !metadataString.equalsIgnoreCase(IndexerConstants.STR_NULL)) metadata = new JsonObject(metadataString);
      JsonObject dataMap = setMetaData(metadata);
      if (dataMap != null && !dataMap.isEmpty()) collectionEo.setMetadata(dataMap);
      
      StatisticsEo statisticsEo = new StatisticsEo();
      // Set Collaborator
      String collaborator = source.getString(EntityAttributeConstants.COLLABORATOR, null);
      Integer collaboratorCount  = 0;
      if (collaborator != null) {
        JsonArray collaboratorIds = new JsonArray(collaborator);
        if (collaboratorIds != null) {
          collectionEo.setCollaboratorIds(collaboratorIds);
        }
        collaboratorCount = collaboratorIds.size();
      }
      statisticsEo.setCollaboratorCount(collaboratorCount);

      // Set Contents of Collection
      List<Map> resourceMetaAsList = getCollectionRepo().getContentsOfCollection(id);
      int questionCount = 0, resourceCount = 0;
      if (resourceMetaAsList != null && resourceMetaAsList.size() > 0) {
        JsonArray resourceIds = new JsonArray();
        JsonArray resourceTitles = new JsonArray();
        JsonArray collectionContents = new JsonArray();
        for (Map resourceMetaMap : resourceMetaAsList) {
          resourceIds.add(resourceMetaMap.get(EntityAttributeConstants.ID).toString());
          resourceTitles.add(resourceMetaMap.get(EntityAttributeConstants.TITLE));
          setCollectionContent(collectionContents, resourceMetaMap);
          String contentFormat = resourceMetaMap.get(EntityAttributeConstants.CONTENT_FORMAT).toString();
          if (contentFormat.equalsIgnoreCase(IndexerConstants.TYPE_QUESTION)) {
            questionCount++;
          } else {
            resourceCount++;
          }
        }
        collectionEo.setResourceIds(resourceIds);
        collectionEo.setResourceTitles(new JsonArray(resourceTitles.stream().distinct().collect(Collectors.toList())));
        collectionEo.setCollectionContents(collectionContents);
      }
      
      //Set course
      CourseEo course = new CourseEo(); 
      course.setId(source.getString(IndexerConstants.COLLECTION_COURSE_ID, null));
      course.setTitle(source.getString(IndexerConstants.COLLECTION_COURSE, null));
      collectionEo.setCourse(course.getCourseJson());
      Boolean isFeatured = false;
      if(course.getId() != null) isFeatured = CourseRepository.instance().isFeatured(course.getId());
      statisticsEo.setFeatured(isFeatured);
      
      // Set Statistics
      statisticsEo.setHasNoThumbnail(thumbnail != null ? 0 : 1);
      statisticsEo.setHasNoDescription(learningObjective != null ? 0 : 1);
      statisticsEo.setQuestionCount(questionCount);
      statisticsEo.setResourceCount(resourceCount);
      statisticsEo.setContentCount(questionCount + resourceCount);
      Long remixedInClassCount = getCollectionRepo().getRemixedInCourseCount(id);
      statisticsEo.setRemixedInCourseCount(remixedInClassCount);
      Long studentCount = getCollectionRepo().getUsedByStudentCount(id);
      statisticsEo.setUsedByStudentCount(studentCount);

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
      
      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      String aggTaxonomy = source.getString(EntityAttributeConstants.AGGREGATED_TAXONOMY, null);
      String aggGutCodes = source.getString(EntityAttributeConstants.AGGREGATED_GUT_CODES, null);
      JsonObject taxonomyObject = null;
      JsonObject aggTaxonomyObject = null;
      JsonObject aggGutCodesObject = null;
      TaxonomyEo taxonomyEo = new TaxonomyEo();
      try {
        if (StringUtils.isNotBlank(taxonomy) && !taxonomy.equalsIgnoreCase(IndexerConstants.STR_NULL)) taxonomyObject = new JsonObject(taxonomy);
        if (StringUtils.isNotBlank(aggTaxonomy) && !aggTaxonomy.equalsIgnoreCase(IndexerConstants.STR_NULL)) aggTaxonomyObject = new JsonObject(aggTaxonomy);
        if (StringUtils.isNotBlank(aggGutCodes) && !aggGutCodes.equalsIgnoreCase(IndexerConstants.STR_NULL)) aggGutCodesObject = new JsonObject(aggGutCodes);
        addTaxonomy(taxonomyObject, taxonomyEo, aggTaxonomyObject, aggGutCodesObject);
      } catch (Exception e) {
        LOGGER.error("Unable to convert Taxonomy to JsonObject", e.getMessage());
      }
      collectionEo.setTaxonomy(taxonomyEo.getTaxonomyJson());
      
      // Set REEf
      Double efficacy = null;
      Double engagement = null;
      JsonObject signatureResource = getIndexRepo().getSignatureResourcesByContentId(collectionEo.getId(), collectionEo.getContentFormat());
      if (signatureResource != null) {
        efficacy = (Double) signatureResource.getValue(EntityAttributeConstants.EFFICACY);
        engagement = (Double) signatureResource.getValue(EntityAttributeConstants.ENGAGEMENT);
      }
      statisticsEo.setEfficacy(efficacy);
      statisticsEo.setEngagement(engagement);
      statisticsEo.setRelevance(null);
      
      long viewsCount = source.getLong(ScoreConstants.VIEW_COUNT);
      int remixCount = source.getInteger(ScoreConstants.COLLECTION_REMIX_COUNT);

      // Use values from statistics index on build index from scratch
      statisticsEo.setViewsCount(viewsCount);
      statisticsEo.setCollectionRemixCount(remixCount);

      // Set Library
      statisticsEo.setLibraryContent(false);
      JsonObject libraryObject = getLibraryRepo().getLibraryContentById(collectionEo.getId());
      if (libraryObject != null && !libraryObject.isEmpty()) {
        JsonObject library = new JsonObject();
        library.put(EntityAttributeConstants.ID, libraryObject.getLong(EntityAttributeConstants.ID));
        library.put(EntityAttributeConstants.NAME, libraryObject.getString(EntityAttributeConstants.NAME));
        library.put(EntityAttributeConstants.DESCRIPTION, libraryObject.getString(EntityAttributeConstants.DESCRIPTION));
        library.put(IndexerConstants.SHORT_NAME, libraryObject.getString(EntityAttributeConstants.SHORT_NAME));
        collectionEo.setLibrary(library);
        statisticsEo.setLibraryContent(true);
      }
      statisticsEo.setLMContent(taxonomyEo.getHasGutStandard() == 1 ? true : false);
      boolean isCuratedContent = false;
      if (SignatureItemsRepository.instance().isCuratedSignatureItemByItemId(id)) isCuratedContent = true;
      statisticsEo.setCuratedContent(isCuratedContent);
      
      Map<String, Object> rankingFields = new HashMap<>();
      rankingFields.put(ScoreConstants.COLLECTION_REMIX_COUNT, remixCount);
      rankingFields.put(ScoreConstants.VIEW_COUNT, viewsCount);
      rankingFields.put(ScoreConstants.COLLAB_COUNT, collaboratorCount);
      rankingFields.put(ScoreConstants.RESOURCE_COUNT, resourceCount);
      rankingFields.put(ScoreConstants.QUESTION_COUNT, questionCount);
      rankingFields.put(ScoreConstants.HAS_NO_THUMBNAIL, statisticsEo.getHasNoThumbnail());
      rankingFields.put(ScoreConstants.DESCRIPTION_FIELD, learningObjective);
      rankingFields.put(ScoreConstants.SATS_HAS_NO_DESC, statisticsEo.getHasNoDescription());
      rankingFields.put(ScoreConstants.ORIGINAL_CONTENT_FIELD, collectionEo.getOriginalCollectionId());
      rankingFields.put(ScoreConstants.PUBLISH_STATUS, collectionEo.getPublishStatus());
      rankingFields.put(ScoreConstants.IS_FEATURED, statisticsEo.isFeatured());
      rankingFields.put(ScoreConstants.GRADING_TYPE, collectionEo.getGradingType());
      JsonObject taxJson = collectionEo.getTaxonomy();
      int hasNoStandard = 1;
      
      if (taxJson != null && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) != null && taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD) == 1) {
        hasNoStandard = 0;
      }

      rankingFields.put(ScoreConstants.TAX_HAS_NO_STANDARD, hasNoStandard);

      double pcWeight = PCWeightUtil.getCollectionPCWeight(new ScoreFields(rankingFields));

      statisticsEo.setPreComputedWeight(pcWeight);
      LOGGER.debug("CollEISB->build : PC weight : " + pcWeight);

      collectionEo.setStatistics(statisticsEo.getStatistics());
      
      // Set license
      Integer licenseId = source.getInteger(EntityAttributeConstants.LICENSE);
      JsonObject license = getLicenseData(licenseId);
      if(license != null){
        collectionEo.setLicense(license);
      }
      
      //Set Collection Tenant 
      String tenantId = source.getString(EntityAttributeConstants.TENANT);
      String tenantRoot = source.getString(EntityAttributeConstants.TENANT_ROOT);
      JsonObject tenant = new JsonObject();
      tenant.put(IndexerConstants.TENANT_ID, tenantId);
      tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
      collectionEo.setTenant(tenant);
      
      //TODO Add logic to store taxonomy transformation and some statistics

    } catch (Exception e) {
      LOGGER.error("ColEISB->build : Collection re-index failed : exception :", e);
      LOGGER.debug("CEISB->build : collection Eo source : " + collectionEo.getCollectionJson().toString());
      throw new Exception(e);

    }
    return collectionEo.getCollectionJson();
  }

  private void setCollectionContent(JsonArray collectionContents, @SuppressWarnings("rawtypes") Map resourceMetaMap) {
    CollectionContentEo content = new CollectionContentEo();
    content.setId(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.ID));
    content.setTitle(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.TITLE));
    content.setUrl(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.URL));
    content.setDescription(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.DESCRIPTION));
    content.setContentFormat(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.CONTENT_FORMAT));
    content.setContentSubFormat(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.CONTENT_SUB_FORMAT));
    content.setThumbnail(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.THUMBNAIL));
    collectionContents.add(content.getCollectionContentJson());
  }

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new CollectionEio());
  }

  @Override
  public String getName() {
    return IndexType.COLLECTION.getType();
  }

}
