package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
      rubricEo.setContentFormat(source.getString(EntityAttributeConstants.CONTENT_FORMAT, null));
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
      JsonObject taxonomyObject = null;
      TaxonomyEo taxonomyEo = new TaxonomyEo();
      try {
        if (taxonomy != null)
          taxonomyObject = new JsonObject(taxonomy);
        addTaxonomy(taxonomyObject, taxonomyEo);
      } catch (Exception e) {
        LOGGER.error("Unable to convert Taxonomy to JsonObject", e.getMessage());
      }
      rubricEo.setTaxonomy(taxonomyEo.getTaxonomyJson());

      // TODO Set Statistics
      StatisticsEo statisticsEo = new StatisticsEo();
      // statisticsEo.setQuestionCount(questionCount);
      rubricEo.setStatistics(statisticsEo.getStatistics());

      // Set course
      CourseEo course = new CourseEo();
      course.setId(source.getString(IndexerConstants.COLLECTION_COURSE_ID, null));
      course.setTitle(source.getString(IndexerConstants.COLLECTION_COURSE, null));
      rubricEo.setCourse(course.getCourseJson());

      // Set unit
      UnitEo unit = new UnitEo();
      unit.setId(source.getString(IndexerConstants.COLLECTION_COURSE_ID, null));
      unit.setTitle(source.getString(IndexerConstants.COLLECTION_COURSE, null));
      rubricEo.setCourse(course.getCourseJson());

      // Set lesson
      LessonEo lesson = new LessonEo();
      lesson.setId(source.getString(IndexerConstants.COLLECTION_COURSE_ID, null));
      lesson.setTitle(source.getString(IndexerConstants.COLLECTION_COURSE, null));
      rubricEo.setCourse(course.getCourseJson());

      // Set collection
      CollectionEo colllection = new CollectionEo();
      colllection.setId(source.getString(IndexerConstants.COLLECTION_COURSE_ID, null));
      colllection.setTitle(source.getString(IndexerConstants.COLLECTION_COURSE, null));
      rubricEo.setCourse(course.getCourseJson());

      // Set content
      ContentEo content = new ContentEo();
      content.setId(source.getString(IndexerConstants.COLLECTION_COURSE_ID, null));
      content.setTitle(source.getString(IndexerConstants.COLLECTION_COURSE, null));
      rubricEo.setCourse(course.getCourseJson());

      // Set Rubric Tenant
      String tenantId = source.getString(EntityAttributeConstants.TENANT);
      String tenantRoot = source.getString(EntityAttributeConstants.TENANT_ROOT);
      JsonObject tenant = new JsonObject();
      tenant.put(IndexerConstants.TENANT_ID, tenantId);
      tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
      rubricEo.setTenant(tenant);

    } catch (Exception e) {
      LOGGER.error("RubEISB->build : Rubric re-index failed : exception :", e);
      LOGGER.debug("REISB->build : Rubric Eo source : " + rubricEo.getRubricJson().toString());
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
