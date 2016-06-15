package org.gooru.nucleus.search.indexers.app.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CodeEo;
import org.gooru.nucleus.search.indexers.app.index.model.LicenseEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomySetEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CollectionRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CollectionRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UserRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UserRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.utils.BaseUtil;
import org.javalite.common.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 */
public abstract class EsIndexSrcBuilder<S, D> implements IsEsIndexSrcBuilder<S, D> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(EsIndexSrcBuilder.class);
  protected static final String dateInputPatterns[] =
          { "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.SSS", "yyyy/MM/dd", "yyyy-MM" };
  protected static final String dateOutputPattern = "yyyy/MM/dd HH:mm:ss";
  protected static final String IS_BUILD_INDEX = "isBuildIndex";
  private static final Map<String, IsEsIndexSrcBuilder<?, ?>> esIndexSrcBuilders = new HashMap<>();

  static {
    registerESIndexSrcBuilders();
  }

  private static void registerESIndexSrcBuilders() {
    esIndexSrcBuilders.put(IndexType.RESOURCE.getType(), new ContentEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.COLLECTION.getType(), new CollectionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.COURSE.getType(), new CourseEsIndexSrcBuilder<>());
  }

  public static IsEsIndexSrcBuilder<?, ?> get(String requestBuilderName) {
    if (esIndexSrcBuilders.containsKey(requestBuilderName)) {
      return esIndexSrcBuilders.get(requestBuilderName);
    } else {
      throw new RuntimeException("Oops! Invalid type : " + requestBuilderName);
    }
  }

  @Override
  public String buildSource(JsonObject source, D destination) throws Exception {
    return build(source, destination).toString();
  }

  protected abstract JsonObject build(JsonObject source, D destination) throws Exception;

  protected CollectionRepositoryImpl getCollectionRepo() {
    return (CollectionRepositoryImpl) CollectionRepository.instance();
  }

  protected UserRepositoryImpl getUserRepo() {
    return (UserRepositoryImpl) UserRepository.instance();
  }

  protected ContentRepositoryImpl getContentRepo() {
    return (ContentRepositoryImpl) ContentRepository.instance();
  }

  protected IndexRepositoryImpl getIndexRepo() {
    return (IndexRepositoryImpl) IndexRepository.instance();
  }

  protected TaxonomyRepositoryImpl getTaxonomyRepo() {
    return (TaxonomyRepositoryImpl) TaxonomyRepository.instance();
  }

  @SuppressWarnings({ "rawtypes"})
  protected void setUser(Map orginalCreator, UserEo userEo) {
    userEo.setUsername(BaseUtil.checkNullAndGetString(orginalCreator, "username"));
    userEo.setUsernameDisplay(BaseUtil.checkNullAndGetString(orginalCreator, "username"));
    userEo.setUserId(BaseUtil.checkNullAndGetString(orginalCreator, "userId"));
    userEo.setLastName(BaseUtil.checkNullAndGetString(orginalCreator, "lastname"));
    userEo.setFirstName(BaseUtil.checkNullAndGetString(orginalCreator, "firstname"));
    userEo.setFullName(
            BaseUtil.checkNullAndGetString(orginalCreator, "firstname") + ' ' + BaseUtil.checkNullAndGetString(orginalCreator, "lastname"));
    userEo.setEmailId(BaseUtil.checkNullAndGetString(orginalCreator, "email_id"));
    if (orginalCreator.get("metadata") != null) {
      JsonObject metadata = new JsonObject(orginalCreator.get("metadata").toString());
      userEo.setProfileVisibility(metadata.getBoolean("is_profile_visible", false));
    }
    userEo.setProfileImage(BaseUtil.checkNullAndGetString(orginalCreator, "thumbnail_path"));
  }

  protected void addTaxonomy(JsonObject taxonomyObject, TaxonomyEo taxonomyEo) {
    JsonArray subjectArray = new JsonArray();
    JsonArray courseArray = new JsonArray();
    JsonArray domainArray = new JsonArray();
    JsonArray standardArray = new JsonArray();
    JsonArray learningTargetArray = new JsonArray();
    JsonArray leafSLInternalCodes = new JsonArray();
    List<String> standardDesc = new ArrayList<>();
    List<String> ltDescArray = new ArrayList<>();

    JsonArray standardDisplayArray = new JsonArray();
    JsonArray ltDisplayArray = new JsonArray();
    JsonArray frameworkCodeArray = new JsonArray();
    JsonArray displayObjectArray = new JsonArray();

    JsonArray subjectLabelArray = new JsonArray();
    JsonArray courseLabelArray = new JsonArray();
    JsonArray domainLabelArray = new JsonArray();

    TaxonomySetEo taxonomyDataSet = new TaxonomySetEo();
    JsonObject curriculumTaxonomy = new JsonObject();

    if (taxonomyObject != null && !taxonomyObject.isEmpty()) {
      for (String code : taxonomyObject.fieldNames()) {
        JsonObject displayCodeJson = taxonomyObject.getJsonObject(code);
        JsonObject displayObject = new JsonObject();

        String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
        String subjectCode = null;
        String courseCode = null;
        String domainCode = null;
                
        if(codes.length > 0){
          
          if(codes.length == 1){
            subjectCode = code;
          }else if(codes.length > 1){
            subjectCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 1));
          }
          if(codes.length == 2){
            courseCode = code;
          }else if(codes.length > 2){
            courseCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 2));
          }
          if(codes.length == 3){
            domainCode = code;
          }else if(codes.length > 3){
            domainCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 3));
          }
          if (codes.length == 4) {
            standardArray.add(code);
            leafSLInternalCodes.add(code);
            standardDesc.add(displayCodeJson.getString(EntityAttributeConstants.TITLE));
            standardDisplayArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
            frameworkCodeArray.add(displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));
            
            setDisplayObject(code, displayCodeJson, displayObject);
            displayObjectArray.add(displayObject);
          }
          if (codes.length == 5) {
            learningTargetArray.add(code);
            leafSLInternalCodes.add(code);
            ltDescArray.add(displayCodeJson.getString(EntityAttributeConstants.TITLE));
            ltDisplayArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
            frameworkCodeArray.add(displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));
            
            setDisplayObject(code, displayCodeJson, displayObject);
            displayObjectArray.add(displayObject);
          }
        }
        
        setTaxonomyMeta(subjectArray, courseArray, domainArray, subjectLabelArray, courseLabelArray, domainLabelArray, subjectCode, courseCode,
                domainCode);
      }
    }
    
    if (subjectArray.size() > 0) taxonomyEo.setSubject(subjectArray);
    if (courseArray.size() > 0)  taxonomyEo.setCourse(courseArray);
    if (domainArray.size() > 0)  taxonomyEo.setDomain(domainArray);
    
    taxonomyEo.setHasStandard(0);
    if (standardArray.size() > 0) {
      taxonomyEo.setHasStandard(1);
      taxonomyEo.setStandards(standardArray);
      taxonomyEo.setStandardsDisplay(standardDisplayArray);
    }
    
    if(learningTargetArray.size() > 0){
      taxonomyEo.setHasStandard(1);
      taxonomyEo.setLearningTargets(learningTargetArray);
      taxonomyEo.setLearningTargetsDisplay(ltDisplayArray);
    }
    
    if(leafSLInternalCodes.size() > 0){
      taxonomyEo.setLeafInternalCodes(leafSLInternalCodes);
    }
    
    taxonomyDataSet.setSubject(subjectLabelArray);
    taxonomyDataSet.setCourse(courseLabelArray);
    taxonomyDataSet.setDomain(domainLabelArray);
    curriculumTaxonomy.put(IndexerConstants.CURRICULUM_CODE, standardDisplayArray != null ? standardDisplayArray : new JsonArray())
            .put(IndexerConstants.CURRICULUM_DESC, standardDesc != null ? standardDesc : new JsonArray())
            .put(IndexerConstants.CURRICULUM_NAME, frameworkCodeArray != null ? frameworkCodeArray.stream().distinct().collect(Collectors.toList()) : new JsonArray())
            .put(IndexerConstants.CURRICULUM_INFO, displayObjectArray != null ? displayObjectArray : new JsonArray());
    taxonomyDataSet.setCurriculum(curriculumTaxonomy);
    taxonomyEo.setTaxonomyDataSet(taxonomyDataSet.getTaxonomyJson().toString());
    taxonomyEo.setTaxonomySet(taxonomyDataSet.getTaxonomyJson());
  }

  private void setDisplayObject(String code, JsonObject displayCodeJson, JsonObject displayObject) {
    displayObject.put(EntityAttributeConstants.ID, code);
    displayObject.put(EntityAttributeConstants.CODE, displayCodeJson.getString(EntityAttributeConstants.CODE));
    displayObject.put(EntityAttributeConstants.TITLE, displayCodeJson.getString(EntityAttributeConstants.TITLE));
    displayObject.put(IndexerConstants.FRAMEWORK_CODE, displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));
    displayObject.put(IndexerConstants.PARENT_TITLE, displayCodeJson.getString(EntityAttributeConstants.TAX_PARENT_TITLE));
  }

  
  @SuppressWarnings("rawtypes")
  private void setTaxonomyMeta(JsonArray subjectArray, JsonArray courseArray, JsonArray domainArray, JsonArray subjectLabelArray,
          JsonArray courseLabelArray, JsonArray domainLabelArray, String subjectCode, String courseCode, String domainCode) {
    if (subjectCode != null) {
      CodeEo subject = new CodeEo();
      List<Map> subjectData = getTaxonomyRepo().getTaxonomyData(subjectCode, IndexerConstants.SUBJECT);
      if (subjectData != null && subjectData.size() > 0) {
        String subjectTitle = subjectData.get(0).get(EntityAttributeConstants.TITLE).toString();
        subjectLabelArray.add(subjectTitle);
        subject.setLabel(subjectTitle);
      }
      subject.setCodeId(subjectCode);
      subjectArray.add(subject.getCode());
    }
    if (courseCode != null) {
      CodeEo course = new CodeEo();
      List<Map> courseData = getTaxonomyRepo().getTaxonomyData(courseCode, IndexerConstants.COURSE);
      if (courseData != null && courseData.size() > 0) {
        String courseTitle = courseData.get(0).get(EntityAttributeConstants.TITLE).toString();
        courseLabelArray.add(courseTitle);
        course.setLabel(courseTitle);
      }
      course.setCodeId(courseCode);
      courseArray.add(course.getCode());
    }
    if (domainCode != null) {
      CodeEo domain = new CodeEo();
      List<Map> domainData = getTaxonomyRepo().getTaxonomyData(domainCode, IndexerConstants.DOMAIN);
      if (domainData != null && domainData.size() > 0) {
        String domainTitle = domainData.get(0).get(EntityAttributeConstants.TITLE).toString();
        domainLabelArray.add(domainTitle);
        domain.setLabel(domainTitle);
      }
      domain.setCodeId(domainCode);
      domainArray.add(domain.getCode());
    }
  }

  @SuppressWarnings("rawtypes")
  protected JsonObject getLicenseData(Integer licenseId){
    if(licenseId != null){
      List<Map> metacontent = getIndexRepo().getLicenseMetadata(licenseId);
      if(metacontent != null && metacontent.size() > 0){
        LicenseEo license = new LicenseEo();
        for (Map metaMap : metacontent) {
          license.setName(metaMap.get(EntityAttributeConstants.LABEL).toString());
          if(metaMap.get(EntityAttributeConstants.META_DATA_INFO) != null){
            String metadataInfo = Convert.toString(metaMap.get(EntityAttributeConstants.META_DATA_INFO));
            if(metadataInfo != null && !metadataInfo.isEmpty()){
              JsonObject licenseMetadataInfo = new JsonObject(metadataInfo).getJsonObject(EntityAttributeConstants.LICENSE);
              if(licenseMetadataInfo != null){
                license.setCode(licenseMetadataInfo.getString(EntityAttributeConstants.LICENSE_CODE));
                license.setDefinition(licenseMetadataInfo.getString(EntityAttributeConstants.LICENSE_DEFINITION));
                license.setIcon(licenseMetadataInfo.getString(EntityAttributeConstants.LICENSE_ICON));
                license.setUrl(licenseMetadataInfo.getString(EntityAttributeConstants.LICENSE_URL));
              }
            }
          }
        }
        return license.getLicense();
      }
    }
    return null;
  }
  
}
