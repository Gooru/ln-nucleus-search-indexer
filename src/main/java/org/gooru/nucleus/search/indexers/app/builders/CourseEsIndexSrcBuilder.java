package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEio;
import org.gooru.nucleus.search.indexers.app.index.model.CourseStatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepository;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CourseEsIndexSrcBuilder<S extends JsonObject, D extends CourseEio> extends EsIndexSrcBuilder<S, D> {
  private static final String PUBLISHED = "published";

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
      LOGGER.debug("CEISB->build : course index source : " + source.toString());
      String id = source.getString(EntityAttributeConstants.ID);

      courseEio.setId(id);
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
      
      int isFeatured = 0;
      if(publishStatus.equalsIgnoreCase(PUBLISHED)){
        isFeatured = 1;
      }
      courseEio.setIsFeatured(isFeatured);
      
      // Set taxonomy data 
      String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
      if(taxonomy != null && !taxonomy.isEmpty()){
        JsonObject taxonomyJson = new JsonObject(taxonomy);
        if(taxonomyJson != null && !taxonomyJson.isEmpty()) {
          JsonArray subjectArray = new JsonArray();
          JsonArray courseArray = new JsonArray();
          JsonArray frameworkCode = new JsonArray();
          for(String code : taxonomyJson.fieldNames()){
            String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
            JsonObject taxData = taxonomyJson.getJsonObject(code);
            if(taxData != null && !taxData.isEmpty()){
              JsonObject subject = new JsonObject();
              if(codes.length == 1){
                subject.put(IndexerConstants.CODE_ID, code);
              }
              else if(codes.length > 1){
                subject.put(IndexerConstants.CODE_ID, code.substring(0, StringUtils.ordinalIndexOf(code, "-", 1)));
              }
              subject.put(IndexerConstants.LABEL, taxData.getString(EntityAttributeConstants.TAX_PARENT_TITLE));
              subjectArray.add(subject);
              
              if(codes.length == 2){
                JsonObject course = new JsonObject();
                course.put(IndexerConstants.CODE_ID, taxData.getString(EntityAttributeConstants.CODE));
                course.put(IndexerConstants.LABEL, taxData.getString(EntityAttributeConstants.TITLE));
                courseArray.add(course);
              }
              if(taxData.getString(EntityAttributeConstants.FRAMEWORK_CODE) != null){
                frameworkCode.add(taxData.getString(EntityAttributeConstants.FRAMEWORK_CODE));
              }
            }
          }
          
          if(courseArray.size() > 0 || subjectArray.size() > 0){
            JsonObject taxonomyObj = new JsonObject();
            taxonomyObj.put(IndexerConstants.SUBJECT, subjectArray);
            taxonomyObj.put(IndexerConstants.COURSE, courseArray);
            taxonomyObj.put(IndexerConstants.FRAMEWORK_CODE, frameworkCode);
            courseEio.setTaxonomy(taxonomyObj);
          }
        }
      }
      
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
        List<Map> orginalCreator = getUserRepo().getUserDetails(originalCreatorId);
        if (orginalCreator != null && orginalCreator.size() > 0) {
          setUser(orginalCreator.get(0), orginalCreatorEo);
          courseEio.setOriginalCreator(orginalCreatorEo.getUser());
        }
      }
      // Set Creator
      String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
      if (creatorId != null) {
        UserEo creatorEo = new UserEo();
        List<Map> creator = getUserRepo().getUserDetails(creatorId);
        if (creator != null && creator.size() > 0) {
          setUser(creator.get(0), creatorEo);
          courseEio.setCreator(creatorEo.getUser());
        }
      }
      // Set Owner
      String ownerId = source.getString(EntityAttributeConstants.OWNER_ID, null);
      if (ownerId != null) {
        UserEo ownerEo = new UserEo();
        List<Map> owner = getUserRepo().getUserDetails(ownerId);
        if (owner != null && owner.size() > 0) {
          setUser(owner.get(0), ownerEo);
          courseEio.setOwner(ownerEo.getUser());
        }
      }

      // Set statistics data 
      Integer unitCount = CourseRepository.instance().getUnitCount(id);
      CourseStatisticsEo statistics = new CourseStatisticsEo();
      statistics.setUnitCount(unitCount);
      courseEio.setStatistics(statistics);
      
      // Set license
      Integer licenseId = source.getInteger(EntityAttributeConstants.LICENSE);
      JsonObject license = getLicenseData(licenseId);
      if(license != null){
        courseEio.setLicense(license);
      }


    }
    catch(Exception e){
      LOGGER.error("build index source for course failed", e);
      throw new Exception(e);
    }
    return courseEio;
  }

}
