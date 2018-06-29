package org.gooru.nucleus.search.indexers.app.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CollectionContentEo;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UnitEio;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Lesson;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UnitEsIndexSrcBuilder<S extends JsonObject, D extends UnitEio> extends EsIndexSrcBuilder<S, D> {


  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new UnitEio());
  }

  @Override
  public String getName() {
    return IndexType.UNIT.getType();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected JsonObject build(JsonObject source, D unitEio) throws Exception {
    try {
      String id = source.getString(EntityAttributeConstants.UNIT_ID);
      unitEio.setId(source.getString(EntityAttributeConstants.UNIT_ID));
      unitEio.setIndexId(id);
      unitEio.setIndexType(getName());
      unitEio.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
      unitEio.setIndexUpdatedTime(new Date());
      unitEio.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      unitEio.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT, null));
      unitEio.setContentFormat(IndexerConstants.UNIT);
      unitEio.setModifierId(source.getString(EntityAttributeConstants.MODIFIER_ID, null));
      unitEio.setOriginalUnitId(source.getString(EntityAttributeConstants.ORIGINAL_UNIT_ID, null));
      unitEio.setParentUnitId(source.getString(EntityAttributeConstants.PARENT_UNIT_ID, null));
      
      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo orginalCreatorEo = new UserEo();
        JsonObject orginalCreator = getUserRepo().getUser(originalCreatorId);
        if (orginalCreator != null && !orginalCreator.isEmpty()) {
          setUser(orginalCreator, orginalCreatorEo);
          unitEio.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        JsonObject creator = getUserRepo().getUser(creatorId);
        if (creator != null && !creator.isEmpty()) {
          setUser(creator, creatorEo);
          unitEio.setCreator(creatorEo.getUser());
        }
      }
      // Set Owner
      String ownerId = source.getString(EntityAttributeConstants.OWNER_ID, null);
      if (ownerId != null) {
        UserEo ownerEo = new UserEo();
        JsonObject owner = getUserRepo().getUser(ownerId);
        if (owner != null && !owner.isEmpty()) {
          setUser(owner, ownerEo);
          unitEio.setOwner(ownerEo.getUser());
        }
      }
      
      StatisticsEo statisticsEo = new StatisticsEo();
      // Set Collaborator
      String collaborator = source.getString(EntityAttributeConstants.COLLABORATOR, null);
      Integer collaboratorCount  = 0;
      if (collaborator != null) {
        JsonArray collaboratorIds = new JsonArray(collaborator);
        if (collaboratorIds != null) {
          unitEio.setCollaboratorIds(collaboratorIds);
        }
        collaboratorCount = collaboratorIds.size();
      }
      statisticsEo.setCollaboratorCount(collaboratorCount);
      
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
      unitEio.setTaxonomy(taxonomyEo.getTaxonomyJson());

      //Set Unit Tenant 
      String tenantId = source.getString(EntityAttributeConstants.TENANT);
      String tenantRoot = source.getString(EntityAttributeConstants.TENANT_ROOT);
      JsonObject tenant = new JsonObject();
      tenant.put(IndexerConstants.TENANT_ID, tenantId);
      tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
      unitEio.setTenant(tenant);

      //Set CUL course mapped
      CourseEo course = new CourseEo(); 
      course.setId(source.getString(EntityAttributeConstants.COURSE_ID));
      JsonObject courseData = getCourseRepo().getCourseById(course.getId()); 
      course.setTitle(courseData.getString(EntityAttributeConstants.TITLE, null));
      unitEio.setCourse(course.getCourseJson());
      unitEio.setPublishStatus(courseData.getString(EntityAttributeConstants.PUBLISH_STATUS));

      Map<String, Object> courseResponse = IndexService.instance().getDocument(course.getId(), IndexNameHolder.getIndexName(EsIndex.COURSE), IndexerConstants.TYPE_COURSE);
      Map<String, Object> taxonomyAsMap = null;
      if (courseResponse != null && !courseResponse.isEmpty()) taxonomyAsMap = (Map<String, Object>) courseResponse.get(IndexFields.TAXONOMY);
      if (taxonomyAsMap != null && !taxonomyAsMap.isEmpty()) {
        List<String> taxSubjectLabels = new ArrayList<String>(); 
        List<String> taxCourseLabels = new ArrayList<String>();
        if (taxonomyAsMap.containsKey(IndexFields.SUBJECT)) {
          JsonArray subjectArray = new JsonArray();
          if (taxonomyEo.getSubject() != null) subjectArray = taxonomyEo.getSubject();
          subjectArray.addAll(new JsonArray((List<Map<String, Object>>) taxonomyAsMap.get(IndexFields.SUBJECT)));
          if (!subjectArray.isEmpty()) taxonomyEo.setSubject(subjectArray);
          List<String> taxSubjectLabelsOfParent = (List<String>) ((Map<String, Object>) taxonomyAsMap.get(IndexFields.TAXONOMY_SET)).get(IndexFields.SUBJECT);
          if (taxonomyEo.getTaxonomySet().getJsonArray(IndexFields.SUBJECT) != null) taxSubjectLabels = taxonomyEo.getTaxonomySet().getJsonArray(IndexFields.SUBJECT).getList();
          if (!taxSubjectLabelsOfParent.isEmpty()) taxSubjectLabels.addAll(taxSubjectLabelsOfParent);
        }
        if (taxonomyAsMap.containsKey(IndexFields.COURSE)) {
          JsonArray courseArray = new JsonArray();
          if (taxonomyEo.getCourse() != null) courseArray = taxonomyEo.getCourse();
          courseArray.addAll(new JsonArray((List<Map<String, Object>>) taxonomyAsMap.get(IndexFields.COURSE)));
          if (!courseArray.isEmpty()) taxonomyEo.setCourse(courseArray);
          List<String> taxCourseLabelsOfParent = (List<String>) ((Map<String, Object>) taxonomyAsMap.get(IndexFields.TAXONOMY_SET)).get(IndexFields.COURSE);
          if (taxonomyEo.getTaxonomySet().getJsonArray(IndexFields.COURSE) != null) taxCourseLabels = taxonomyEo.getTaxonomySet().getJsonArray(IndexFields.COURSE).getList();
          if (!taxCourseLabelsOfParent.isEmpty()) taxCourseLabels.addAll(taxCourseLabelsOfParent);
        }
        JsonObject taxonomyDataSet = taxonomyEo.getTaxonomySet();
        taxonomyDataSet.put(IndexFields.SUBJECT, taxSubjectLabels.stream().distinct().collect(Collectors.toList()))
        .put(IndexFields.COURSE, taxCourseLabels.stream().distinct().collect(Collectors.toList()));
        taxonomyEo.setTaxonomySet(taxonomyDataSet);
        unitEio.setTaxonomy(taxonomyEo.getTaxonomyJson());
      }
      
      LazyList<Lesson> mappedLessons = getLessonRepo().getLessonByUnitId(unitEio.getId());
      if (mappedLessons != null && !mappedLessons.isEmpty()) {
        JsonArray lessonIds = new JsonArray();
        JsonArray lessonTitles = new JsonArray();
        mappedLessons.forEach(lesson -> {
          lessonIds.add(lesson.getString(EntityAttributeConstants.LESSON_ID));
          lessonTitles.add(lesson.getString(EntityAttributeConstants.TITLE));
        });
        unitEio.setLessonIds(lessonIds);
        unitEio.setLessonTitles(lessonTitles);
      }
      
      //Set Collection Details
      JsonArray collectionTitles = new JsonArray();
      JsonArray collectionContainerIds = new JsonArray();
      JsonArray collectionIds = new JsonArray();
      JsonArray assessmentIds = new JsonArray();
      JsonArray externalAssessmentIds = new JsonArray();
      JsonArray collectionContents = new JsonArray();
      LazyList<Collection> collectionData = getCollectionRepo().getCollectionsByUnitId(id);
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
          setUnitContents(collectionContents, collection);
        });
      }
      if (!collectionIds.isEmpty()) unitEio.setCollectionIds(collectionContainerIds);
      if (!collectionTitles.isEmpty()) unitEio.setCollectionTitles(new JsonArray(collectionTitles.stream().distinct().collect(Collectors.toList())));
      if (!collectionContents.isEmpty()) unitEio.setCollections(collectionContents);

      Boolean isFeatured = CourseRepository.instance().isFeatured(course.getId());
      statisticsEo.setFeatured(isFeatured);
      long viewsCount = source.getLong(IndexFields.VIEWS_COUNT, 0L);
      statisticsEo.setViewsCount(viewsCount);
      statisticsEo.setLessonCount((unitEio.getLessonIds() != null && !unitEio.getLessonIds().isEmpty()) ? unitEio.getLessonIds().size() : 0);
      statisticsEo.setContainingCollectionsCount((!collectionContainerIds.isEmpty()) ? collectionContainerIds.size() : 0);
      statisticsEo.setCollectionCount((!collectionIds.isEmpty()) ? collectionIds.size() : 0);
      statisticsEo.setAssessmentCount((!assessmentIds.isEmpty()) ? assessmentIds.size() : 0);
      statisticsEo.setExternalAssessmentCount((!externalAssessmentIds.isEmpty()) ? externalAssessmentIds.size() : 0);

      // Set REEf
      Double efficacy = null;
      Double engagement = null;
      JsonObject signatureResource = getIndexRepo().getSignatureResourcesByContentId(unitEio.getId(), unitEio.getContentFormat());
      if (signatureResource != null) {
        efficacy = (Double) signatureResource.getValue(EntityAttributeConstants.EFFICACY);
        engagement = (Double) signatureResource.getValue(EntityAttributeConstants.ENGAGEMENT);
      }
      statisticsEo.setEfficacy(efficacy);
      statisticsEo.setEngagement(engagement);
      statisticsEo.setRelevance(null);
      
      // Set Library
      statisticsEo.setLibraryContent(false);
      JsonObject libraryObject = getLibraryRepo().getLibraryContentById(unitEio.getId());
      if (libraryObject != null && !libraryObject.isEmpty()) {
        JsonObject library = new JsonObject();
        library.put(EntityAttributeConstants.ID, libraryObject.getLong(EntityAttributeConstants.ID));
        library.put(EntityAttributeConstants.NAME, libraryObject.getString(EntityAttributeConstants.NAME));
        library.put(EntityAttributeConstants.DESCRIPTION, libraryObject.getString(EntityAttributeConstants.DESCRIPTION));
        library.put(IndexerConstants.SHORT_NAME, libraryObject.getString(EntityAttributeConstants.SHORT_NAME));
        unitEio.setLibrary(library);
        statisticsEo.setLibraryContent(true);
      }
      
      //TODO Add logic to store statistics
      unitEio.setStatistics(statisticsEo.getStatistics());
    } catch (Exception e) {
      LOGGER.error("UEISB->build : Failed to build source : Exception", e);
      LOGGER.debug("UEISB -> build : unitEio Eo source : " + unitEio.getUnitJson().toString());
      throw new Exception(e);
    }
    return unitEio.getUnitJson();
  }
  
  private void setUnitContents(JsonArray collectionContents, Collection collectionData) {
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
