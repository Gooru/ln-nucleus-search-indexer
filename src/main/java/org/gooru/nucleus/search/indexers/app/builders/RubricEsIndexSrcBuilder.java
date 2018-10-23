package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CollectionEo;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEo;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.LessonEo;
import org.gooru.nucleus.search.indexers.app.index.model.RubricEio;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UnitEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;

import com.google.common.base.CaseFormat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RubricEsIndexSrcBuilder<S extends JsonObject, D extends RubricEio> extends EsIndexSrcBuilder<S, D> {

  @Override
  public JsonObject build(JsonObject source, D rubricEo) throws Exception {
    try {
      String id = source.getString(EntityAttributeConstants.ID);
      rubricEo.setId(source.getString(EntityAttributeConstants.ID));
      rubricEo.setIndexId(id);
      rubricEo.setIndexType(getName());
      rubricEo.setUrl(source.getString(EntityAttributeConstants.URL, null));
      rubricEo.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
      rubricEo.setDescription(source.getString(EntityAttributeConstants.DESCRIPTION, null));
      rubricEo.setContentFormat(source.getString(EntityAttributeConstants.FORMAT, null));
      rubricEo.setIndexUpdatedTime(new Date());
      rubricEo.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      rubricEo.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT, null));
      rubricEo.setOriginalRubricId(source.getString(EntityAttributeConstants.ORIGINAL_RUBRIC_ID, null));
      rubricEo.setParentRubricId(source.getString(EntityAttributeConstants.PARENT_RUBRIC_ID, null));
      rubricEo.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
      rubricEo.setPublishStatus(source.getString(EntityAttributeConstants.PUBLISH_STATUS, null));
      rubricEo.setContentFormat(IndexerConstants.TYPE_RUBRIC);
      String thumbnail = source.getString(EntityAttributeConstants.THUMBNAIL, null);
      rubricEo.setThumbnail(thumbnail);
      rubricEo.setModifierId(source.getString(EntityAttributeConstants.MODIFIER_ID, null));
      // Set Original Creator
      String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
      if (originalCreatorId != null) {
        UserEo originalCreatorEo = new UserEo();
        JsonObject originalCreator = getUserRepo().getUser(originalCreatorId);
        if (originalCreator != null && !originalCreator.isEmpty()) {
          setUser(originalCreator, originalCreatorEo);
          rubricEo.setOriginalCreator(originalCreatorEo.getUser());
        }
      }
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        JsonObject creator = getUserRepo().getUser(creatorId);
        if (creator != null && !creator.isEmpty()) {
          setUser(creator, creatorEo);
          rubricEo.setCreator(creatorEo.getUser());
        }
      }

      // TODO Set Catagory
      // rubricEo.setCategories(categories);

      // Set Metadata
      String metadataString = source.getString(EntityAttributeConstants.METADATA, null);
      JsonObject metadata = null;
      if (StringUtils.isNotBlank(metadataString) && !metadataString.equalsIgnoreCase(IndexerConstants.STR_NULL)) metadata = new JsonObject(metadataString);
      setMetaData(metadata, rubricEo);

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
      rubricEo.setTaxonomy(taxonomyEo.getTaxonomyJson());

      // Set course
      CourseEo course = new CourseEo();
      course.setId(source.getString(EntityAttributeConstants.COURSE_ID, null));
      if (course.getId() != null) {
        JsonObject courseData = getCourseRepo().getCourseById(course.getId());
        course.setTitle(courseData.getString(EntityAttributeConstants.TITLE, null));
      }
      rubricEo.setCourse(course.getCourseJson());

      // Set unit
      UnitEo unit = new UnitEo();
      unit.setId(source.getString(EntityAttributeConstants.UNIT_ID, null));
      if (unit.getId() != null) {
        JsonObject unitData = getUnitRepo().getUnitById(unit.getId());
        unit.setTitle(unitData.getString(EntityAttributeConstants.TITLE, null));
      }
      rubricEo.setUnit(unit.getUnitJson());

      // Set lesson
      LessonEo lesson = new LessonEo();
      lesson.setId(source.getString(EntityAttributeConstants.LESSON_ID, null));
      if (lesson.getId() != null) {
        JsonObject lessonData = getLessonRepo().getLessonById(lesson.getId());
        lesson.setTitle(lessonData.getString(EntityAttributeConstants.TITLE, null));
      }
      rubricEo.setLesson(lesson.getLessonJson());

      // Set collection
      CollectionEo collection = new CollectionEo();
      collection.setId(source.getString(EntityAttributeConstants.COLLECTION_ID, null));
      if (collection.getId() != null) {
        JsonObject collectionData = getCollectionRepo().getCollectionById(collection.getId());
        collection.setTitle(collectionData.getString(EntityAttributeConstants.TITLE, null));
      }
      rubricEo.setCollection(collection.getCollectionJson());

      // Set content
      ContentEo content = new ContentEo();
      content.setId(source.getString(EntityAttributeConstants.CONTENT_ID, null));
      if (content.getId() != null) {
        JsonObject contentData = getContentRepo().getQuestionById(content.getId());
        content.setTitle(contentData.getString(EntityAttributeConstants.TITLE, null));
      }
      rubricEo.setContent(content.getContentJson());
        
      StatisticsEo statisticsEo = new StatisticsEo();
      Integer questionCount = getRubricRepo().getQuestionCountByRubricId(id);
      if (content.getId() != null) questionCount++;
      statisticsEo.setQuestionCount(questionCount);
      
      //Set Library
      statisticsEo.setLibraryContent(false);
      JsonObject libraryObject = getLibraryRepo().getLibraryContentById(rubricEo.getId());
      if (libraryObject != null && !libraryObject.isEmpty()) {
        JsonObject library = new JsonObject();
        library.put(EntityAttributeConstants.ID, libraryObject.getLong(EntityAttributeConstants.ID));
        library.put(EntityAttributeConstants.NAME, libraryObject.getString(EntityAttributeConstants.NAME));
        library.put(EntityAttributeConstants.DESCRIPTION, libraryObject.getString(EntityAttributeConstants.DESCRIPTION));
        library.put(IndexerConstants.SHORT_NAME, libraryObject.getString(EntityAttributeConstants.SHORT_NAME));
        rubricEo.setLibrary(library);
        statisticsEo.setLibraryContent(true);
      }
      statisticsEo.setLMContent(taxonomyEo.getHasGutStandard() == 1 ? true : false);
      rubricEo.setStatistics(statisticsEo.getStatistics());

      // Set Rubric Tenant
      String tenantId = source.getString(EntityAttributeConstants.TENANT);
      String tenantRoot = source.getString(EntityAttributeConstants.TENANT_ROOT);
      JsonObject tenant = new JsonObject();
      tenant.put(IndexerConstants.TENANT_ID, tenantId);
      tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
      rubricEo.setTenant(tenant);

    } catch (Exception e) {
      LOGGER.error("RubEISB->build : Rubric re-index failed : exception :", e);
      LOGGER.debug("RubEISB->build : Rubric Eo source : " + rubricEo.getRubricJson().toString());
      throw new Exception(e);
    }
    return rubricEo.getRubricJson();
  }

  protected void setMetaData(JsonObject metaData, RubricEio rubricEo) {
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
            rubricEo.setMetadata(dataMap);
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
  
  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new RubricEio());
  }

  @Override
  public String getName() {
    return IndexType.RUBRIC.getType();
  }

}
