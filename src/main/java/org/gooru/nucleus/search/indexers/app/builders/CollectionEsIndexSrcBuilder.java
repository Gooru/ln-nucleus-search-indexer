package org.gooru.nucleus.search.indexers.app.builders;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.*;
import org.gooru.nucleus.search.indexers.app.utils.BaseUtil;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author SearchTeam
 */
public class CollectionEsIndexSrcBuilder<S extends JsonObject, D extends CollectionEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("rawtypes")
  @Override
  public JsonObject build(JsonObject source, D collectionEo) throws Exception {
    try {
      LOGGER.debug("CEISB->build : index source : " + source.toString());
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
      String thumbnail = source.getString(EntityAttributeConstants.THUMBNAIL, null);
      collectionEo.setThumbnail(thumbnail);
      String learningObjective = source.getString(EntityAttributeConstants.LEARNING_OBJECTIVE, null);
      collectionEo.setLearningObjective(learningObjective);
      collectionEo.setAudience(source.getJsonArray(EntityAttributeConstants.AUDIENCE, null));
      collectionEo.setOrientation(source.getString(EntityAttributeConstants.ORIENTATION, null));
      collectionEo.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));
      collectionEo.setGradingType(source.getString(EntityAttributeConstants.GRADING, null));
      collectionEo.setModifierId(source.getString(EntityAttributeConstants.MODIFIER_ID, null));
      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo orginalCreatorEo = new UserEo();
        List<Map> orginalCreator = getUserRepo().getUserDetails(originalCreatorId);
        if (orginalCreator != null && orginalCreator.size() > 0) {
          setUser(orginalCreator.get(0), orginalCreatorEo);
          collectionEo.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        List<Map> creator = getUserRepo().getUserDetails(originalCreatorId);
        if (creator != null && creator.size() > 0) {
          setUser(creator.get(0), creatorEo);
          collectionEo.setCreator(creatorEo.getUser());
        }
      }
      // Set Owner
      String ownerId = source.getString(EntityAttributeConstants.OWNER_ID, null);
      if (ownerId != null) {
        UserEo ownerEo = new UserEo();
        List<Map> owner = getUserRepo().getUserDetails(originalCreatorId);
        if (owner != null && owner.size() > 0) {
          setUser(owner.get(0), ownerEo);
          collectionEo.setOriginalCreator(ownerEo.getUser());
        }
      }
      // Set Metadata
      String metadataString = source.getString(EntityAttributeConstants.METADATA, null);
      if (metadataString != null) {
        JsonObject metadata = new JsonObject(metadataString);
        if (metadata != null) {
          JsonObject metadataAsMap = new JsonObject();
          for (String fieldName : metadata.fieldNames()) {
            String key = IndexerConstants.getMetadataIndexAttributeName(fieldName);
            JsonArray references = metadata.getJsonArray(fieldName);
            if (references != null) {
              JsonArray value = new JsonArray();
              String referenceIds = references.toString();
              List<Map> metacontent = getIndexRepo().getMetadata(referenceIds.substring(1, referenceIds.length() - 1));
              for (Map metaMap : metacontent) {
                value.add(metaMap.get(EntityAttributeConstants.LABEL).toString().toLowerCase().replaceAll("[^\\dA-Za-z]", "_"));
              }
              if (!value.isEmpty()) metadataAsMap.put(key, value);
              if (!metadataAsMap.isEmpty()) collectionEo.setMetadata(metadataAsMap);
            }
          }
        }
      }
      StatisticsEo statisticsEo = new StatisticsEo();
      // Set Collaborator
      String collaborator = source.getString(EntityAttributeConstants.COLLABORATOR, null);
      Integer collaboratorSize = 0;
      if (collaborator != null) {
        JsonArray collaboratorIds = new JsonArray(collaborator);
        if (collaboratorIds != null) {
          collectionEo.setCollaboratorIds(collaboratorIds);
        }
        collaboratorSize = collaboratorIds.size();
      }
      statisticsEo.setCollaboratorCount(collaboratorSize);
      
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
      // Set Statistics
      statisticsEo.setHasNoThumbnail(thumbnail != null ? 0 : 1);
      statisticsEo.setHasNoDescription(learningObjective != null ? 0 : 1);
      statisticsEo.setQuestionCount(questionCount);
      statisticsEo.setResourceCount(resourceCount);
      statisticsEo.setContentCount(questionCount + resourceCount);

      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      if (taxonomy != null) {
        JsonArray taxonomyArray = new JsonArray(taxonomy);
        TaxonomyEo taxonomyEo = new TaxonomyEo();
        if (taxonomyArray.size() > 0) {
          addTaxnomy(taxonomyArray, taxonomyEo);
        }
        collectionEo.setTaxonomy(taxonomyEo.getTaxonomyJson());
      }

      long viewsCount = source.getLong(ScoreConstants.VIEW_COUNT);
      int remixCount = source.getInteger(ScoreConstants.COLLECTION_REMIX_COUNT);
      int collaboratorCount = source.getInteger(ScoreConstants.COLLAB_COUNT);

      // Use values from statistics index on build index from scratch
      if (source.getBoolean(IS_BUILD_INDEX) != null && source.getBoolean(IS_BUILD_INDEX)) {
        statisticsEo.setViewsCount(viewsCount);
        statisticsEo.setCollectionRemixCount(remixCount);
        statisticsEo.setCollaboratorCount(collaboratorCount);
      }

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

      JsonObject taxJson = collectionEo.getTaxonomy();
      int hasStandard = 0;
      if (taxJson != null) {
        hasStandard = taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD);
      }

      rankingFields.put(ScoreConstants.TAX_HAS_STANDARD, hasStandard);

      statisticsEo.setPreComputedWeight(PCWeightUtil.getCollectionPCWeight(new ScoreFields(rankingFields)));

      collectionEo.setStatistics(statisticsEo.getStatistics());
      
      //TODO Add logic to store taxonomy transformation and some statistics
      LOGGER.debug("CEISB->build : collection Eo source : " + collectionEo.getCollectionJson().toString());

    } catch (Exception e) {
      LOGGER.error("ColEISB->build : Collection re-index failed : exception :", e);
      throw new Exception(e);

    }
    return collectionEo.getCollectionJson();
  }

  private void setCollectionContent(JsonArray collectionContents, @SuppressWarnings("rawtypes") Map resourceMetaMap) {
    CollectionContentEo content = new CollectionContentEo();
    content.setId(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.ID));
    content.setTitle(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.TITLE));
    content.setUrl(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.URL));
    content.setShortTitle(BaseUtil.checkNullAndGetString(resourceMetaMap, EntityAttributeConstants.SHORT_TITLE));
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
