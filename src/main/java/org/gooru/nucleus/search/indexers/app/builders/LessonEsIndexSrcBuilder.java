package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CollectionContentEo;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.LessonEio;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UnitEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Collection;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class LessonEsIndexSrcBuilder<S extends JsonObject, D extends LessonEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new LessonEio());
  }

  @Override
  public String getName() {
    return IndexType.LESSON.getType();
  }

  @Override
  protected JsonObject build(JsonObject source, D lessonEio) throws Exception {
    try {
      String id = source.getString(EntityAttributeConstants.LESSON_ID);
      lessonEio.setId(source.getString(EntityAttributeConstants.LESSON_ID));
      lessonEio.setIndexId(id);
      lessonEio.setIndexType(getName());
      lessonEio.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
      lessonEio.setContentFormat(source.getString(EntityAttributeConstants.FORMAT, null));
      lessonEio.setIndexUpdatedTime(new Date());
      lessonEio.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      lessonEio.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT, null));
      lessonEio.setContentFormat(IndexerConstants.LESSON);
      lessonEio.setModifierId(source.getString(EntityAttributeConstants.MODIFIER_ID, null));
      lessonEio.setOriginalLessonId(source.getString(EntityAttributeConstants.ORIGINAL_LESSON_ID, null));
      lessonEio.setParentLessonId(source.getString(EntityAttributeConstants.PARENT_LESSON_ID, null));
      
      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo orginalCreatorEo = new UserEo();
        JsonObject orginalCreator = getUserRepo().getUser(originalCreatorId);
        if (orginalCreator != null && !orginalCreator.isEmpty()) {
          setUser(orginalCreator, orginalCreatorEo);
          lessonEio.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        JsonObject creator = getUserRepo().getUser(creatorId);
        if (creator != null && !creator.isEmpty()) {
          setUser(creator, creatorEo);
          lessonEio.setCreator(creatorEo.getUser());
        }
      }
      // Set Owner
      String ownerId = source.getString(EntityAttributeConstants.OWNER_ID, null);
      if (ownerId != null) {
        UserEo ownerEo = new UserEo();
        JsonObject owner = getUserRepo().getUser(ownerId);
        if (owner != null && !owner.isEmpty()) {
          setUser(owner, ownerEo);
          lessonEio.setOwner(ownerEo.getUser());
        }
      }
      
      StatisticsEo statisticsEo = new StatisticsEo();
      
      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      JsonObject taxonomyObject = null;
      TaxonomyEo taxonomyEo = new TaxonomyEo();
      try {
        if (taxonomy != null) taxonomyObject = new JsonObject(taxonomy);
        addTaxonomy(taxonomyObject, taxonomyEo);
      } catch (Exception e) {
        LOGGER.error("Unable to convert Taxonomy to JsonObject", e.getMessage());
      }
      lessonEio.setTaxonomy(taxonomyEo.getTaxonomyJson());

      //Set Lesson Tenant 
      String tenantId = source.getString(EntityAttributeConstants.TENANT);
      String tenantRoot = source.getString(EntityAttributeConstants.TENANT_ROOT);
      JsonObject tenant = new JsonObject();
      tenant.put(IndexerConstants.TENANT_ID, tenantId);
      tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
      lessonEio.setTenant(tenant);

      //Set CUL course mapped
      CourseEo course = new CourseEo(); 
      course.setId(source.getString(EntityAttributeConstants.COURSE_ID));
      JsonObject courseData = getCourseRepo().getCourseById(course.getId());
      course.setTitle(courseData.getString(EntityAttributeConstants.TITLE, null));
      lessonEio.setCourse(course.getCourseJson());
      lessonEio.setPublishStatus(courseData.getString(EntityAttributeConstants.PUBLISH_STATUS));
      
      //Set CUL unit mapped
      UnitEo unit = new UnitEo(); 
      unit.setId(source.getString(EntityAttributeConstants.UNIT_ID));
      JsonObject unitData = getUnitRepo().getUnitById(unit.getId());
      unit.setTitle(unitData.getString(EntityAttributeConstants.TITLE, null));
      lessonEio.setUnit(unit.getUnitJson());
      
      //Set Collection Details
      JsonArray collectionTitles = new JsonArray();
      JsonArray collectionContainerIds = new JsonArray();
      JsonArray collectionIds = new JsonArray();
      JsonArray assessmentIds = new JsonArray();
      JsonArray externalAssessmentIds = new JsonArray();
      JsonArray collectionContents = new JsonArray();
      LazyList<Collection> collectionData = getCollectionRepo().getCollectionsByLessonId(id);
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
          setLessonContents(collectionContents, collection);
        });
      }
      if (!collectionIds.isEmpty()) lessonEio.setCollectionIds(collectionContainerIds);
      if (!collectionTitles.isEmpty()) lessonEio.setCollectionTitles(new JsonArray(collectionTitles.stream().distinct().collect(Collectors.toList())));
      if (!collectionContents.isEmpty()) lessonEio.setCollections(collectionContents);
      
      Boolean isFeatured = CourseRepository.instance().isFeatured(course.getId());
      statisticsEo.setFeatured(isFeatured);
      long viewsCount = source.getLong(IndexFields.VIEWS_COUNT, 0L);
      statisticsEo.setViewsCount(viewsCount);
      statisticsEo.setContainingCollectionsCount((!collectionContainerIds.isEmpty()) ? collectionContainerIds.size() : 0);
      statisticsEo.setCollectionCount((!collectionIds.isEmpty()) ? collectionIds.size() : 0);
      statisticsEo.setAssessmentCount((!assessmentIds.isEmpty()) ? assessmentIds.size() : 0);
      statisticsEo.setExternalAssessmentCount((!externalAssessmentIds.isEmpty()) ? externalAssessmentIds.size() : 0);

      // Set REEf
      Double efficacy = null;
      Double engagement = null;
      JsonObject signatureResource = getOriginalResourceRepo().getSignatureResources(lessonEio.getId(), lessonEio.getContentFormat());
      if (signatureResource != null) {
        efficacy = (Double) signatureResource.getValue(EntityAttributeConstants.EFFICACY);
        engagement = (Double) signatureResource.getValue(EntityAttributeConstants.ENGAGEMENT);
      }
      statisticsEo.setEfficacy(efficacy);
      statisticsEo.setEngagement(engagement);
      statisticsEo.setRelevance(null);
      
      //TODO Add logic to store statistics - remixed in units, course, class count
      lessonEio.setStatistics(statisticsEo.getStatistics());


      int invalidResource = 0;
      if(StringUtils.trimToNull(lessonEio.getTitle()) == null){
        invalidResource = 1;
      }
      statisticsEo.setInvalidResource(invalidResource);
      
    } catch (Exception e) {
      LOGGER.error("LEISB->build : Failed to build source : Exception", e);
      LOGGER.debug("LEISB -> build : lessonEio Eo source : " + lessonEio.getLessonJson().toString());
      throw new Exception(e);
    }
    return lessonEio.getLessonJson();
  }

  private void setLessonContents(JsonArray collectionContents, Collection collectionData) {
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
