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
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentVectorRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentVectorRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CourseRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LessonRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LessonRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LibraryRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LibraryRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.MachineClassifyContentRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.MachineClassifyContentRepositoryImpl;
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

import com.google.common.base.CaseFormat;

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
    esIndexSrcBuilders.put(IndexType.QUESTION.getType(), new QuestionAndResourceReferenceEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.RESOURCE.getType(), new ResourceEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.RESOURCE_REFERENCE.getType(), new QuestionAndResourceReferenceEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.COLLECTION.getType(), new CollectionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.ASSESSMENT.getType(), new CollectionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.COLLECTION_EXTERNAL.getType(), new CollectionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.ASSESSMENT_EXTERNAL.getType(), new CollectionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.OFFLINE_ACTIVITY.getType(), new CollectionEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.COURSE.getType(), new CourseEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.CROSSWALK.getType(), new CrosswalkEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.UNIT.getType(), new UnitEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.LESSON.getType(), new LessonEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.RUBRIC.getType(), new RubricEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.TAXONOMY.getType(), new TaxonomyEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.TENANT.getType(), new TenantEsIndexSrcBuilder<>());
    esIndexSrcBuilders.put(IndexType.GUT.getType(), new GutEsIndexSrcBuilder<>());
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
  
  protected MachineClassifyContentRepositoryImpl getMachineClassifiedTagsRepo() {
    return (MachineClassifyContentRepositoryImpl) MachineClassifyContentRepository.instance();
  }
  
  protected LibraryRepositoryImpl getLibraryRepo() {
    return (LibraryRepositoryImpl) LibraryRepository.instance();
  }
  
  protected ContentVectorRepositoryImpl getContentVectorRepo() {
    return (ContentVectorRepositoryImpl) ContentVectorRepository.instance();
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
        if (code.contains(IndexerConstants.HYPHEN_SEPARATOR)) {
          JsonObject displayCodeJson = taxonomyObject.getJsonObject(code);
          JsonObject displayObject = new JsonObject();

          String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);

          if (codes.length > 0) {
            if (codes.length == 4) {
              standardArray.add(code);
              leafSLInternalCodes.add(code);
              if (!displayCodeJson.isEmpty()) {
                leafSLDisplayCodes.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
                standardDesc.add(displayCodeJson.getString(EntityAttributeConstants.TITLE));
                standardDisplayArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
                frameworkCodeArray.add(displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));

                setDisplayObject(code, displayCodeJson, displayObject);
                displayObjectArray.add(displayObject);
              }
            }
            if (codes.length == 5) {
              learningTargetArray.add(code);
              leafSLInternalCodes.add(code);
              if (!displayCodeJson.isEmpty()) {
                leafSLDisplayCodes.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
                ltDescArray.add(displayCodeJson.getString(EntityAttributeConstants.TITLE));
                ltDisplayArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
                frameworkCodeArray.add(displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));

                setDisplayObject(code, displayCodeJson, displayObject);
                displayObjectArray.add(displayObject);
              }
            }

            leafSLInternalCodes.add(code);
            if (!displayCodeJson.isEmpty()) {
              leafSLDisplayCodes.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
              setDisplayObject(code, displayCodeJson, displayObject);
              displayObjectArray.add(displayObject);
            }
          }

          extractSCDAndSetTaxMeta(code, subjectArray, courseArray, domainArray, subjectLabelArray, courseLabelArray, domainLabelArray);
        }
      }
    }
    
    taxonomyEo.setHasStandard(0);
    taxonomyEo.setHasGutStandard(0);
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
      JsonObject eqSLObject = setEquivalentCodes(leafAggInternalCodes, aggTaxonomyObject);
      if (!(eqSLObject.getJsonArray("gutCodes")).isEmpty()) {
        relatedGutCodes.addAll(eqSLObject.getJsonArray("gutCodes"));
      }
      JsonArray leafEqInternalCodes = new JsonArray();
      if (taxonomyEo.getAllEquivalentInternalCodes() != null) leafEqInternalCodes = taxonomyEo.getAllEquivalentInternalCodes();
      if (!(eqSLObject.getJsonArray("eqInternalCodesArray")).isEmpty())  leafEqInternalCodes.addAll(eqSLObject.getJsonArray("eqInternalCodesArray"));
      if (leafEqInternalCodes != null && !leafEqInternalCodes.isEmpty()) taxonomyEo.setAllEqRelatedInternalCodes(leafEqInternalCodes);
    }
    if (!relatedGutCodes.isEmpty()) {
      taxonomyEo.setRelatedGutCodes(relatedGutCodes);
      relatedGutCodes.forEach(c -> {
        String code = (String) c;
        extractSCDAndSetTaxMeta(code, subjectArray, courseArray, domainArray, subjectLabelArray, courseLabelArray, domainLabelArray);
        if(((String[]) code.split(IndexerConstants.HYPHEN_SEPARATOR)).length >= 4) taxonomyEo.setHasGutStandard(1);
      });
    }
    
    if (subjectArray.size() > 0) taxonomyEo.setSubject(subjectArray);
    if (courseArray.size() > 0) taxonomyEo.setCourse(courseArray);
    if (domainArray.size() > 0) taxonomyEo.setDomain(domainArray);
    
    taxonomyDataSet.setSubject(subjectLabelArray);
    taxonomyDataSet.setCourse(courseLabelArray);
    taxonomyDataSet.setDomain(domainLabelArray);
    
    curriculumTaxonomy.put(IndexFields.CURRICULUM_CODE, standardDisplayArray != null ? standardDisplayArray : new JsonArray())
            .put(IndexFields.CURRICULUM_DESC, standardDesc != null ? standardDesc : new JsonArray())
            .put(IndexFields.CURRICULUM_NAME, frameworkCodeArray != null ? frameworkCodeArray.stream().distinct().collect(Collectors.toList()) : new JsonArray())
            .put(IndexFields.CURRICULUM_INFO, displayObjectArray != null ? displayObjectArray.stream().distinct().collect(Collectors.toList()) : new JsonArray());
    taxonomyDataSet.setCurriculum(curriculumTaxonomy);
    //taxonomyEo.setTaxonomyDataSet(taxonomyDataSet.getTaxonomyJson().toString());
    taxonomyEo.setTaxonomySet(taxonomyDataSet.getTaxonomyJson());
  }

  private String extractDomain(String code) {
    String domainCode = null;
    String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length == 3) {
      domainCode = code;
    } else if (codes.length > 3) {
      domainCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 3));
    }
    return domainCode;
  }

  private String extractCourse(String code) {
    String courseCode = null;
    String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length == 2) {
      courseCode = code;
    } else if (codes.length > 2) {
      courseCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 2));
    }
    return courseCode;
  }

  private String extractSubject(String code) {
    String subjectCode = null;
    String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length == 1) {
      subjectCode = code;
    } else if (codes.length > 1) {
      subjectCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 1));
    }
    return subjectCode;
  }

  private void setDisplayObject(String code, JsonObject displayCodeJson, JsonObject displayObject) {
    displayObject.put(EntityAttributeConstants.ID, code);
    displayObject.put(EntityAttributeConstants.CODE, displayCodeJson.getString(EntityAttributeConstants.CODE));
    displayObject.put(EntityAttributeConstants.TITLE, displayCodeJson.getString(EntityAttributeConstants.TITLE));
    displayObject.put(IndexFields.FRAMEWORK_CODE, displayCodeJson.getString(EntityAttributeConstants.FRAMEWORK_CODE));
    displayObject.put(IndexFields.PARENT_TITLE, displayCodeJson.getString(EntityAttributeConstants.TAX_PARENT_TITLE));
  }

  private void extractSCDAndSetTaxMeta(String code, JsonArray subjectArray, JsonArray courseArray, JsonArray domainArray, JsonArray subjectLabelArray,
          JsonArray courseLabelArray, JsonArray domainLabelArray) {
    extractSubjectMeta(code, subjectArray, subjectLabelArray);
    extractCourseMeta(code, courseArray, courseLabelArray);
    extractDomainMeta(code, domainArray, domainLabelArray);
  }

  @SuppressWarnings("rawtypes")
  private void extractDomainMeta(String code, JsonArray domainArray, JsonArray domainLabelArray) {
    String domainCode = extractDomain(code);
    if (domainCode != null) {
      CodeEo domain = new CodeEo();
      CodeEo gutCode = new CodeEo();
      List<Map> domainData = getTaxonomyRepo().getTaxonomyData(domainCode, IndexerConstants.DOMAIN);
      if (domainData != null && domainData.size() > 0) {
        String domainTitle = domainData.get(0).get(EntityAttributeConstants.TITLE).toString();
        domainLabelArray.add(domainTitle);
        domain.setLabel(domainTitle);
        if (domainData.get(0).get(EntityAttributeConstants.DEFAULT_TAXONOMY_DOMAIN_ID) != null) {
          gutCode.setCodeId(domainData.get(0).get(EntityAttributeConstants.DEFAULT_TAXONOMY_DOMAIN_ID).toString());
          domainArray.add(gutCode.getCode());
        }
      }
      domain.setCodeId(domainCode);
      domainArray.add(domain.getCode());
    }
  }

  @SuppressWarnings("rawtypes")
  private void extractCourseMeta(String code, JsonArray courseArray, JsonArray courseLabelArray) {
    String courseCode = extractCourse(code);
    if (courseCode != null) {
      CodeEo course = new CodeEo();
      CodeEo gutCode = new CodeEo();
      List<Map> courseData = getTaxonomyRepo().getTaxonomyData(courseCode, IndexerConstants.COURSE);
      if (courseData != null && courseData.size() > 0) {
        String courseTitle = courseData.get(0).get(EntityAttributeConstants.TITLE).toString();
        courseLabelArray.add(courseTitle);
        course.setLabel(courseTitle);
        if (courseData.get(0).get(EntityAttributeConstants.DEFAULT_TAXONOMY_COURSE_ID) != null) {
          gutCode.setCodeId(courseData.get(0).get(EntityAttributeConstants.DEFAULT_TAXONOMY_COURSE_ID).toString());
          courseArray.add(gutCode.getCode());
        }
      }
      course.setCodeId(courseCode);
      courseArray.add(course.getCode());
    }
  }

  @SuppressWarnings("rawtypes")
  private void extractSubjectMeta(String code, JsonArray subjectArray, JsonArray subjectLabelArray) {
    String subjectCode = extractSubject(code);
    if (subjectCode != null) {
      CodeEo subject = new CodeEo();
      CodeEo gutCode = new CodeEo();
      List<Map> subjectData = getTaxonomyRepo().getTaxonomyData(subjectCode, IndexerConstants.SUBJECT);
      if (subjectData != null && subjectData.size() > 0) {
        String subjectTitle = subjectData.get(0).get(EntityAttributeConstants.TITLE).toString();
        subjectLabelArray.add(subjectTitle);
        subject.setLabel(subjectTitle);
        subject.setSubjectClassification(subjectData.get(0).get(EntityAttributeConstants.SUBJECT_CLASSIFICATION).toString());
        if (subjectData.get(0).get(EntityAttributeConstants.DEFAULT_TAXONOMY_SUBJECT_ID) != null) {
          gutCode.setCodeId(subjectData.get(0).get(EntityAttributeConstants.DEFAULT_TAXONOMY_SUBJECT_ID).toString());
          subjectArray.add(gutCode.getCode());
        }
      }
      subject.setCodeId(subjectCode);
      subjectArray.add(subject.getCode());
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
        if (displayCodeJson != null && !displayCodeJson.isEmpty() && displayCodeJson.getString(EntityAttributeConstants.CODE) != null)
          eqDisplayCodesArray.add(displayCodeJson.getString(EntityAttributeConstants.CODE));
      }
      JsonArray gdtArray = getTaxonomyRepo().getGDTCode(leafCode);
      if (gdtArray != null && gdtArray.size() > 0) {
        gdtArray.forEach(a -> {
          JsonObject gdtCode = (JsonObject) a;
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
  
  protected JsonObject setMetaData(JsonObject metaData) {
    JsonObject dataMap = new JsonObject();
    if (metaData != null) {
      for (String fieldName : metaData.fieldNames()) {
        // Temp logic to only process array fields
        Object metaValue = metaData.getValue(fieldName);
        if (metaValue instanceof JsonArray) {
          JsonObject extractedMeta = extractMetaValues(metaData, fieldName);
          JsonArray value = extractedMeta.getJsonArray(IndexerConstants.VALUE);
          String key = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, fieldName);
          if (value != null && !value.isEmpty())
            dataMap.put(key, value);
          if (extractedMeta.containsKey(IndexerConstants.TWCS)) {
            JsonArray twcs = (JsonArray) extractedMeta.getValue(IndexerConstants.TWCS);
            dataMap.put(IndexerConstants.TWCS, twcs);
          }
        }
      }
    }
    return dataMap;
  }

  @SuppressWarnings("rawtypes")
  private JsonObject extractMetaValues(JsonObject metadata, String fieldName){
    JsonObject metaObject = new JsonObject();
    JsonArray value = new JsonArray();
    JsonArray references = metadata.getJsonArray(fieldName);
    if (references != null && references.size() > 0) {
      String referenceIds = references.toString().substring(1, references.toString().length() - 1);
      List<Map> metacontent = null;
      if (fieldName.equalsIgnoreCase(EntityAttributeConstants.TWENTY_ONE_CENTURY_SKILL)) {
        metacontent = getIndexRepo().getTwentyOneCenturySkill(referenceIds);
      } else {
        metacontent = getIndexRepo().getMetadata(referenceIds);
      }
      if (metacontent != null) {
        List<Map<String, String>> twcsList = new ArrayList<>();
        for (Map metaMap : metacontent) {
          if (metaMap.containsKey(EntityAttributeConstants.LABEL)) value.add(metaMap.get(EntityAttributeConstants.LABEL).toString());
          if (fieldName.equalsIgnoreCase(EntityAttributeConstants.TWENTY_ONE_CENTURY_SKILL)) {
              Map<String, String> classification = new HashMap<>();
              classification.put(IndexFields.CODE, metaMap.get(EntityAttributeConstants.LABEL).toString());
              classification.put(IndexFields.CLASSIFICATION, metaMap.get(EntityAttributeConstants.KEY_CLASSIFICATION).toString());
              for (String key : IndexerConstants.TW_FRAMEWORKS) {
                 add21csFwClassification(twcsList, metaMap, classification, key);
              }
          }
        }
        if (!twcsList.isEmpty()) metaObject.put(IndexerConstants.TWCS, twcsList);
      }
    }
    metaObject.put(IndexerConstants.VALUE, value);
    return metaObject;
  }

    @SuppressWarnings("rawtypes")
    private void add21csFwClassification(List<Map<String, String>> twcsList, Map metaMap,
        Map<String, String> classification, String key) {
        if (Boolean.valueOf(metaMap.get(key).toString()))  {
              Map<String, String> twcs = new HashMap<>();
              key = key.replaceAll("_model", IndexerConstants.EMPTY_STRING).replaceAll(IndexerConstants.UNDERSCORE, " ");
              twcs.put(IndexFields.FRAMEWORK, key);
              twcs.putAll(classification);
              twcsList.add(twcs);
          }
    }
    
  protected JsonObject getPrimaryLanguage(Integer primaryLanguageId) {
    if (primaryLanguageId != null) {
      JsonObject language = getIndexRepo().getLanguages(primaryLanguageId);
      if (language != null) {
        return language;
      }
    }
    return null;
  }

}
