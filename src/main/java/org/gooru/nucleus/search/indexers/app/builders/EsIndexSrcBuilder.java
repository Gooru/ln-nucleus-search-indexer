package org.gooru.nucleus.search.indexers.app.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchRegistry;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
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
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LessonRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LessonRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.OriginalResourceRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.OriginalResourceRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.RubricRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.RubricRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.SignatureItemsRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.SignatureItemsRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TenantRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TenantRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UnitRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UnitRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UserRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UserRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
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
  protected static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");
  protected static final String dateInputPatterns[] =
          { "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.SSS", "yyyy/MM/dd", "yyyy-MM" };
  protected static final String dateOutputPattern = "yyyy/MM/dd HH:mm:ss";
  protected static final String IS_BUILD_INDEX = "isBuildIndex";
  private static final Map<String, IsEsIndexSrcBuilder<?, ?>> esIndexSrcBuilders = new HashMap<>();

  static {
    registerESIndexSrcBuilders();
  }

  private static void registerESIndexSrcBuilders() {
    esIndexSrcBuilders.put(IndexType.QUESTION.getType(), new QuestionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.RESOURCE.getType(), new ResourceEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.COLLECTION.getType(), new CollectionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.COURSE.getType(), new CourseEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.CROSSWALK.getType(), new CrosswalkEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.UNIT.getType(), new UnitEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.LESSON.getType(), new LessonEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.RUBRIC.getType(), new RubricEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.TAXONOMY.getType(), new TaxonomyEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.TENANT.getType(), new TenantEsIndexSrcBuilder<>());
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
  
  protected CourseRepositoryImpl getCourseRepo() {
    return (CourseRepositoryImpl) CourseRepository.instance();
  }
  
  protected UnitRepositoryImpl getUnitRepo() {
    return (UnitRepositoryImpl) UnitRepository.instance();
  }
  
  protected LessonRepositoryImpl getLessonRepo() {
    return (LessonRepositoryImpl) LessonRepository.instance();
  }
  
  protected RubricRepositoryImpl getRubricRepo() {
    return (RubricRepositoryImpl) RubricRepository.instance();
  }
  
  protected TaxonomyCodeRepositoryImpl getTaxonomyCodeRepo() {
    return (TaxonomyCodeRepositoryImpl) TaxonomyCodeRepository.instance();
  }
  
  protected TenantRepositoryImpl getTenantRepo() {
    return (TenantRepositoryImpl) TenantRepository.instance();
  }

  protected OriginalResourceRepositoryImpl getOriginalResourceRepo() {
    return (OriginalResourceRepositoryImpl) OriginalResourceRepository.instance();
  }
  
  protected SignatureItemsRepositoryImpl getSignatureItemsRepo() {
    return (SignatureItemsRepositoryImpl) SignatureItemsRepository.instance();
  }
  
  protected RestHighLevelClient getClient() {
    return ElasticSearchRegistry.getRestHighLevelClient();
  }
  
  protected void setUser(JsonObject user, UserEo userEo) {
    userEo.setUsername(user.getString("display_name"));
    userEo.setUsernameDisplay(user.getString("display_name"));
    userEo.setUserId(user.getString("id"));
    userEo.setLastName(user.getString("last_name"));
    userEo.setFirstName(user.getString("first_name"));
    userEo.setFullName(user.getString("first_name") + ' ' + user.getString("last_name"));
    userEo.setEmailId(user.getString("email"));
    if (user.containsKey("metadata") && user.getString("metadata")!= null) {
      String metadataString = user.getString("metadata");
      if (StringUtils.isNotBlank(metadataString) && !metadataString.equalsIgnoreCase(IndexerConstants.STR_NULL)) {
        JsonObject metadata = new JsonObject(user.getString("metadata"));
        userEo.setProfileVisibility(metadata.getBoolean("is_profile_visible", false));
      }
    }
    userEo.setProfileImage(user.getString("thumbnail"));
    //Set User Tenant
    String tenantId = user.getString(EntityAttributeConstants.TENANT_ID);
    String tenantRoot = user.getString(EntityAttributeConstants.TENANT_ROOT);
    JsonObject tenant = new JsonObject();
    tenant.put(IndexerConstants.TENANT_ID, tenantId);
    tenant.put(IndexerConstants.TENANT_ROOT_ID, tenantRoot);
    userEo.setTenant(tenant);
  }

  protected void addTaxonomy(JsonObject taxonomyObject, TaxonomyEo taxonomyEo, JsonObject aggTaxonomyObject, JsonObject aggGutCodesObject) {
    JsonArray subjectArray = new JsonArray();
    JsonArray courseArray = new JsonArray();
    JsonArray domainArray = new JsonArray();
    JsonArray standardArray = new JsonArray();
    JsonArray learningTargetArray = new JsonArray();
    JsonArray leafSLInternalCodes = new JsonArray();
    JsonArray leafSLDisplayCodes = new JsonArray();
    JsonArray leafAggInternalCodes = new JsonArray();
    JsonArray leafAggGutCodes = new JsonArray();
    JsonArray relatedLeafInternalCodes = new JsonArray();
    JsonArray relatedGutCodes = new JsonArray();
    List<String> standardDesc = new ArrayList<>();
    List<String> ltDescArray = new ArrayList<>();
    List<String> domainCodeMap = new ArrayList<>();
    List<String> courseCodeMap = new ArrayList<>();

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
        if(code.contains(IndexerConstants.HYPHEN_SEPARATOR)) {
        JsonObject displayCodeJson = taxonomyObject.getJsonObject(code);
        JsonObject displayObject = new JsonObject();

        String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
        String subjectCode = null;
        String courseCode = null;
        String domainCode = null;
                
        if (codes.length > 0) {

          if (codes.length == 1) {
            subjectCode = code;
          } else if (codes.length > 1) {
            subjectCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 1));
          }
          if (codes.length == 2) {
            courseCode = code;
          } else if (codes.length > 2) {
            courseCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 2));
          }
          if (courseCode != null) {
            courseCodeMap.add(courseCode);
          }
          if (codes.length == 3) {
            domainCode = code;
          } else if (codes.length > 3) {
            domainCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 3));
          }
          if (domainCode != null) {
            domainCodeMap.add(domainCode);
          }
          if (codes.length == 4) {
            standardArray.add(code);
            leafSLInternalCodes.add(code);
            leafSLDisplayCodes.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
            standardDesc.add(displayCodeJson.getString(EntityAttributeConstants.TITLE));
            standardDisplayArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
            frameworkCodeArray.add(displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));

            setDisplayObject(code, displayCodeJson, displayObject);
            displayObjectArray.add(displayObject);
          }
          if (codes.length == 5) {
            learningTargetArray.add(code);
            leafSLInternalCodes.add(code);
            leafSLDisplayCodes.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
            ltDescArray.add(displayCodeJson.getString(EntityAttributeConstants.TITLE));
            ltDisplayArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
            frameworkCodeArray.add(displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));

            setDisplayObject(code, displayCodeJson, displayObject);
            displayObjectArray.add(displayObject);
          }

          leafSLInternalCodes.add(code);
          leafSLDisplayCodes.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
          setDisplayObject(code, displayCodeJson, displayObject);
          displayObjectArray.add(displayObject);
        }
        
        setTaxonomyMeta(subjectArray, courseArray, domainArray, subjectLabelArray, courseLabelArray, domainLabelArray, subjectCode, courseCode,
                domainCode);
      }
      }
    }
    
    if (subjectArray.size() > 0) taxonomyEo.setSubject(subjectArray);
    if (courseArray.size() > 0)  { 
      taxonomyEo.setCourse(courseArray);
/*      JsonObject eqCourseObject = setEquivalentCodes(courseCodeMap, null);
      if (!(eqCourseObject.getJsonArray("eqInternalCodesArray")).isEmpty()) taxonomyEo.setAllEqCourseInternalCodes(eqCourseObject.getJsonArray("eqInternalCodesArray"));
   */ }
    if (domainArray.size() > 0)  {
      taxonomyEo.setDomain(domainArray);
/*      JsonObject eqDomainObject = setEquivalentCodes(domainCodeMap, null);
      if (!(eqDomainObject.getJsonArray("eqInternalCodesArray")).isEmpty()) taxonomyEo.setAllEqDomainInternalCodes(eqDomainObject.getJsonArray("eqInternalCodesArray"));
    */}
    
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
    
    if (aggTaxonomyObject != null && !aggTaxonomyObject.isEmpty()) {
      leafAggInternalCodes = new JsonArray(aggTaxonomyObject.fieldNames().stream().distinct().collect(Collectors.toList()));
      relatedLeafInternalCodes = leafAggInternalCodes;
      if (!leafAggInternalCodes.isEmpty()) taxonomyEo.setLeafAggInternalCodes(leafAggInternalCodes);
    }
    if (aggGutCodesObject != null && !aggGutCodesObject.isEmpty()) {
      leafAggGutCodes = new JsonArray(aggGutCodesObject.fieldNames().stream().distinct().collect(Collectors.toList()));
      relatedGutCodes = leafAggGutCodes;
      if (!leafAggGutCodes.isEmpty()) taxonomyEo.setLeafAggGutCodes(leafAggGutCodes);
    }
    
    if(leafSLInternalCodes.size() > 0){
      relatedLeafInternalCodes.addAll(leafSLInternalCodes);
      taxonomyEo.setLeafInternalCodes(leafSLInternalCodes);
      taxonomyEo.setLeafDisplayCodes(leafSLDisplayCodes);
      JsonObject eqSLObject = setEquivalentCodes(leafSLInternalCodes, taxonomyObject);
      if (!(eqSLObject.getJsonArray("gutCodes")).isEmpty()) {
        taxonomyEo.setGutCodes(eqSLObject.getJsonArray("gutCodes"));
        relatedGutCodes.addAll(taxonomyEo.getGutCodes());
      }
      if (!(eqSLObject.getJsonArray("eqInternalCodesArray")).isEmpty()) taxonomyEo.setAllEquivalentInternalCodes(eqSLObject.getJsonArray("eqInternalCodesArray"));
      if (!(eqSLObject.getJsonArray("eqDisplayCodesArray")).isEmpty()) taxonomyEo.setAllEquivalentDisplayCodes(eqSLObject.getJsonArray("eqDisplayCodesArray"));
    }
        
    if (!relatedLeafInternalCodes.isEmpty()) {
      taxonomyEo.setRelatedLeafInternalCodes(relatedLeafInternalCodes);
      JsonObject eqSLObject = setEquivalentCodes(leafAggInternalCodes, taxonomyObject);
      if (!(eqSLObject.getJsonArray("gutCodes")).isEmpty()) {
        relatedGutCodes.addAll(eqSLObject.getJsonArray("gutCodes"));
        taxonomyEo.setRelatedGutCodes(relatedGutCodes);
      }
      JsonArray leafEqIntenralCodes = taxonomyEo.getAllEquivalentInternalCodes();
      if (!(eqSLObject.getJsonArray("eqInternalCodesArray")).isEmpty())  leafEqIntenralCodes.addAll(eqSLObject.getJsonArray("eqInternalCodesArray"));
      if (!leafEqIntenralCodes.isEmpty()) taxonomyEo.setAllEqRelatedInternalCodes(leafEqIntenralCodes);
    }
    if (!relatedGutCodes.isEmpty()) taxonomyEo.setRelatedGutCodes(relatedGutCodes);

    taxonomyDataSet.setSubject(subjectLabelArray);
    taxonomyDataSet.setCourse(courseLabelArray);
    taxonomyDataSet.setDomain(domainLabelArray);
    
    curriculumTaxonomy.put(IndexFields.CURRICULUM_CODE, standardDisplayArray != null ? standardDisplayArray : new JsonArray())
            .put(IndexFields.CURRICULUM_DESC, standardDesc != null ? standardDesc : new JsonArray())
            .put(IndexFields.CURRICULUM_NAME, frameworkCodeArray != null ? frameworkCodeArray.stream().distinct().collect(Collectors.toList()) : new JsonArray())
            .put(IndexFields.CURRICULUM_INFO, displayObjectArray != null ? displayObjectArray.stream().distinct().collect(Collectors.toList()) : new JsonArray());
    taxonomyDataSet.setCurriculum(curriculumTaxonomy);
    taxonomyEo.setTaxonomyDataSet(taxonomyDataSet.getTaxonomyJson().toString());
    taxonomyEo.setTaxonomySet(taxonomyDataSet.getTaxonomyJson());
  }

  private void setDisplayObject(String code, JsonObject displayCodeJson, JsonObject displayObject) {
    displayObject.put(EntityAttributeConstants.ID, code);
    displayObject.put(EntityAttributeConstants.CODE, displayCodeJson.getString(EntityAttributeConstants.CODE));
    displayObject.put(EntityAttributeConstants.TITLE, displayCodeJson.getString(EntityAttributeConstants.TITLE));
    displayObject.put(IndexFields.FRAMEWORK_CODE, displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));
    displayObject.put(IndexFields.PARENT_TITLE, displayCodeJson.getString(EntityAttributeConstants.TAX_PARENT_TITLE));
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
        subject.setSubjectClassification(subjectData.get(0).get(EntityAttributeConstants.SUBJECT_CLASSIFICATION).toString());
      }
      subject.setCodeId(subjectCode);
      subjectArray.add(subject.getCode());
      JsonObject gdtCode = getTaxonomyRepo().getGDTCode(subjectCode);
      if (gdtCode != null && !gdtCode.isEmpty()) {
        CodeEo gutSubject = new CodeEo();
        gutSubject.setCodeId(gdtCode.getString(TaxonomyCode.SOURCE_TAXONOMY_CODE_ID));
        subjectArray.add(gutSubject.getCode());
      }
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
      JsonObject gdtCode = getTaxonomyRepo().getGDTCode(courseCode);
      if (gdtCode != null && !gdtCode.isEmpty()) {
        CodeEo gutCode = new CodeEo();
        gutCode.setCodeId(gdtCode.getString(TaxonomyCode.SOURCE_TAXONOMY_CODE_ID));
        courseArray.add(gutCode.getCode());
      }
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
      JsonObject gdtCode = getTaxonomyRepo().getGDTCode(domainCode);
      if (gdtCode != null && !gdtCode.isEmpty()) {
        CodeEo gutCode = new CodeEo();
        gutCode.setCodeId(gdtCode.getString(TaxonomyCode.SOURCE_TAXONOMY_CODE_ID));
        domainArray.add(gutCode.getCode());
      }
    }
  }
  
  @SuppressWarnings("rawtypes")
  private JsonObject setEquivalentCodes(JsonArray leafSLCodesArray, JsonObject taxonomyObject) {
    JsonArray eqInternalCodesArray = new JsonArray();
    JsonArray eqDisplayCodesArray = new JsonArray();
    JsonArray gutCodes = new JsonArray();
    JsonObject eqObject = new JsonObject();
    leafSLCodesArray.forEach(l -> {
      String leafCode = (String) l;
      eqInternalCodesArray.add(leafCode);
      if (taxonomyObject != null) {
        JsonObject displayCodeJson = taxonomyObject.getJsonObject(leafCode);
        if (!displayCodeJson.isEmpty() && displayCodeJson.getString(EntityAttributeConstants.CODE) != null)
          eqDisplayCodesArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
      }
      JsonObject gdtCode = getTaxonomyRepo().getGDTCode(leafCode);
      if (gdtCode != null && !gdtCode.isEmpty()) {
        gutCodes.add(gdtCode.getString(TaxonomyCode.SOURCE_TAXONOMY_CODE_ID));
        List<Map> equivalentCompetencyList = getTaxonomyRepo().getEquivalentCompetencies(gdtCode.getString(TaxonomyCode.SOURCE_TAXONOMY_CODE_ID));
        if (equivalentCompetencyList != null && !equivalentCompetencyList.isEmpty()) {
          equivalentCompetencyList.forEach(equivalentCompetency -> {
            eqInternalCodesArray.add(equivalentCompetency.get(TaxonomyCode.TARGET_TAXONOMY_CODE_ID).toString());
            eqDisplayCodesArray.add(equivalentCompetency.get(TaxonomyCode.TARGET_DISPLAY_CODE).toString());
          });
        }
      }
    });
    eqObject.put("gutCodes", gutCodes);
    eqObject.put("eqInternalCodesArray", eqInternalCodesArray);
    eqObject.put("eqDisplayCodesArray", eqDisplayCodesArray);
    return eqObject;
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
