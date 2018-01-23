package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CollectionContentEo;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEio;
import org.gooru.nucleus.search.indexers.app.index.model.CourseStatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.ResourceInfoEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Lesson;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Unit;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CourseEsIndexSrcBuilder<S extends JsonObject, D extends CourseEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new CourseEio());
  }

  @Override
  public String getName() {
    return IndexType.COURSE.getType();
  }

  @Override
  protected JsonObject build(JsonObject source, D courseEio) throws Exception {
    try{
      String id = source.getString(EntityAttributeConstants.ID);

      courseEio.setId(id);
      courseEio.setIndexId(id);
      courseEio.setIndexType(getName());
      courseEio.setTitle(source.getString(EntityAttributeConstants.TITLE));
      courseEio.setOriginalCourseId(source.getString(EntityAttributeConstants.ORIGINAL_COURSE_ID, null));
      courseEio.setParentCourseId(source.getString(EntityAttributeConstants.PARENT_COURSE_ID, null));
      courseEio.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      courseEio.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT, null));
      courseEio.setIndexUpdatedTime(new Date());
      courseEio.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));
      courseEio.setModifierId(source.getString(EntityAttributeConstants.MODIFIER_ID, null));
      courseEio.setSubjectBucket(source.getString(EntityAttributeConstants.SUBJECT_BUCKET, null));
      courseEio.setDescription(source.getString(EntityAttributeConstants.DESCRIPTION, null));
      courseEio.setThumbnail(source.getString(EntityAttributeConstants.THUMBNAIL, null));
      courseEio.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
      String publishStatus = source.getString(EntityAttributeConstants.PUBLISH_STATUS);
      courseEio.setPublishStatus(publishStatus);
      courseEio.setContentFormat(IndexerConstants.COURSE);

      Boolean isFeatured = false;
      if(publishStatus.equalsIgnoreCase(IndexerConstants.PUBLISHED)){
        isFeatured = true;
      }
      
      // Set taxonomy data 
      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      JsonObject taxonomyObject = null;
      TaxonomyEo taxonomyEo = new TaxonomyEo();
      try {
        if (taxonomy != null) taxonomyObject = new JsonObject(taxonomy);
        addTaxonomy(taxonomyObject, taxonomyEo);
      } catch (Exception e) {
        LOGGER.error("Unable to convert Taxonomy to JsonObject", e.getMessage());
      }
      courseEio.setTaxonomy(taxonomyEo.getTaxonomyJson());
      
      // Set sequence data
      String editorialStr = source.getString(EntityAttributeConstants.EDITORIAL_TAGS, null);
      if(editorialStr != null && ! editorialStr.isEmpty()){
        JsonObject editorialTags = new JsonObject(editorialStr);
        if(editorialTags != null){
          courseEio.setSubjectSequence(editorialTags.getInteger(EntityAttributeConstants.SUBJECT_SEQUENCE));
          courseEio.setSequenceId(editorialTags.getInteger(EntityAttributeConstants.SEQUENCE_ID));
        }
      }
      
      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo orginalCreatorEo = new UserEo();
        JsonObject orginalCreator = getUserRepo().getUser(originalCreatorId);
        if (orginalCreator != null && !orginalCreator.isEmpty()) {
          setUser(orginalCreator, orginalCreatorEo);
          courseEio.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        JsonObject creator = getUserRepo().getUser(creatorId);
        if (creator != null && !creator.isEmpty()) {
          setUser(creator, creatorEo);
          courseEio.setCreator(creatorEo.getUser());
        }
      }
      // Set Owner
      String ownerId = source.getString(EntityAttributeConstants.OWNER_ID, null);
      if (ownerId != null) {
        UserEo ownerEo = new UserEo();
        JsonObject owner = getUserRepo().getUser(ownerId);
        if (owner != null && !owner.isEmpty()) {
          setUser(owner, ownerEo);
          courseEio.setOwner(ownerEo.getUser());
        }
      }
      
      // Set license
      Integer licenseId = source.getInteger(EntityAttributeConstants.LICENSE);
      JsonObject license = getLicenseData(licenseId);
      if(license != null){
        courseEio.setLicense(license);
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
      if(!resourceInfoJson.getResourceInfo().isEmpty()) courseEio.setResourceInfo(resourceInfoJson.getResourceInfo());

      //Set Content Tenant
      String tenantId = source.getString(EntityAttributeConstants.TENANT);
      String tenantRoot = source.getString(EntityAttributeConstants.TENANT_ROOT);
      JsonObject tenant = new JsonObject();
      tenant.put(IndexerConstants.TENANT_ID, tenantId);
      tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
      courseEio.setTenant(tenant);
      
      //Set Unit Details
      LazyList<Unit> mappedUnits = getUnitRepo().getUnitByCourseId(courseEio.getId());
      if (mappedUnits != null && !mappedUnits.isEmpty()) {
        JsonArray unitIds = new JsonArray();
        JsonArray unitTitles = new JsonArray();
        mappedUnits.forEach(unit -> {
          unitIds.add(unit.getString(EntityAttributeConstants.UNIT_ID));
          unitTitles.add(unit.getString(EntityAttributeConstants.TITLE));
        });
        courseEio.setUnitIds(unitIds);
        courseEio.setUnitTitles(unitTitles);
      }
      
      //Set Lesson Details
      LazyList<Lesson> mappedLessons = getLessonRepo().getLessonByCourseId(courseEio.getId());
      if (mappedLessons != null && !mappedLessons.isEmpty()) {
        JsonArray lessonIds = new JsonArray();
        JsonArray lessonTitles = new JsonArray();
        mappedLessons.forEach(lesson -> {
          lessonIds.add(lesson.getString(EntityAttributeConstants.LESSON_ID));
          lessonTitles.add(lesson.getString(EntityAttributeConstants.TITLE));
        });
        courseEio.setLessonIds(lessonIds);
        courseEio.setLessonTitles(lessonTitles);
      }
      
      //Set Collection Details
      JsonArray collectionTitles = new JsonArray();
      JsonArray collectionContainerIds = new JsonArray();
      JsonArray collectionIds = new JsonArray();
      JsonArray assessmentIds = new JsonArray();
      JsonArray externalAssessmentIds = new JsonArray();
      JsonArray collectionContents = new JsonArray();
      LazyList<Collection> collectionData = getCollectionRepo().getCollectionsByCourseId(id);
      if (collectionData != null && !collectionData.isEmpty()) {
        collectionData.forEach(collection -> {
          String collectionId = collection.getString(EntityAttributeConstants.ID);
          String format = collection.getString(EntityAttributeConstants.FORMAT);
          collectionContainerIds.add(collectionId);
          collectionTitles.add(collection.getString(EntityAttributeConstants.TITLE));
          if (format.equalsIgnoreCase(IndexerConstants.COLLECTION)) {
            collectionIds.add(collectionId);
          } else if (format.equalsIgnoreCase(IndexerConstants.ASSESSMENT)) {
            assessmentIds.add(collectionId);
          } else if (format.equalsIgnoreCase(IndexerConstants.ASSESSMENT_EXTERNAL)) {
            externalAssessmentIds.add(collectionId);
          }
          setCourseContents(collectionContents, collection);
        });
      }
      if (!collectionIds.isEmpty()) courseEio.setCollectionIds(collectionContainerIds);
      if (!collectionTitles.isEmpty()) courseEio.setCollectionTitles(new JsonArray(collectionTitles.stream().distinct().collect(Collectors.toList())));
      if (!collectionContents.isEmpty()) courseEio.setCollections(collectionContents);
      
      // Set statistics data 
      CourseStatisticsEo statistics = new CourseStatisticsEo();
      statistics.setFeatured(isFeatured);
      statistics.setUnitCount((courseEio.getUnitIds() != null && !courseEio.getUnitIds().isEmpty()) ? courseEio.getUnitIds().size() : 0);
      statistics.setLessonCount((courseEio.getLessonIds() != null && !courseEio.getLessonIds().isEmpty()) ? courseEio.getLessonIds().size() : 0L);
      statistics.setContainingCollectionsCount((!collectionContainerIds.isEmpty()) ? collectionContainerIds.size() : 0L);
      statistics.setCollectionCount((!collectionIds.isEmpty()) ? collectionIds.size() : 0L);
      statistics.setAssessmentCount((!assessmentIds.isEmpty()) ? assessmentIds.size() : 0L);
      statistics.setExternalAssessmentCount((!externalAssessmentIds.isEmpty()) ? externalAssessmentIds.size() : 0L);
      statistics.setViewsCount(source.getLong(IndexFields.VIEWS_COUNT, 0L));
      statistics.setCourseRemixCount(source.getInteger(IndexFields.COURSE_REMIXCOUNT, 0));
      Long remixedInClassCount = getCourseRepo().getRemixedInClassCount(id);
      statistics.setRemixedInClassCount(remixedInClassCount);
      Long studentCount = getCourseRepo().getUsedByStudentCount(id);
      statistics.setUsedByStudentCount(studentCount);
      
      // Set REEf
      Double efficacy = null;
      Double engagement = null;
      JsonObject signatureResource = getIndexRepo().getSignatureResourcesByContentId(courseEio.getId(), courseEio.getContentFormat());
      if (signatureResource != null) {
        efficacy = (Double) signatureResource.getValue(EntityAttributeConstants.EFFICACY);
        engagement = (Double) signatureResource.getValue(EntityAttributeConstants.ENGAGEMENT);
      }
      statistics.setEfficacy(efficacy);
      statistics.setEngagement(engagement);
      statistics.setRelevance(null);
      
      courseEio.setStatistics(statistics);
    }
    catch(Exception e){
      LOGGER.error("build index source for course failed", e);
      LOGGER.debug("Es source course json ", courseEio.toString());
      throw new Exception(e);
    }
    return courseEio;
  }

  private void setCourseContents(JsonArray collectionContents, Collection collectionData) {
    CollectionContentEo content = new CollectionContentEo();
    content.setId(collectionData.getString(EntityAttributeConstants.ID));
    content.setTitle(collectionData.getString(EntityAttributeConstants.TITLE));
    content.setUrl(collectionData.getString(EntityAttributeConstants.URL));
    content.setDescription(collectionData.getString(EntityAttributeConstants.LEARNING_OBJECTIVE));
    content.setContentFormat(collectionData.getString(EntityAttributeConstants.FORMAT));
    content.setContentSubFormat(collectionData.getString(EntityAttributeConstants.SUB_FORMAT));
    content.setThumbnail(collectionData.getString(EntityAttributeConstants.THUMBNAIL));
    collectionContents.add(content.getCollectionContentJson());
  }
}
