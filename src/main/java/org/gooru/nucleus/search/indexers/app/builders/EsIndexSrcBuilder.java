package org.gooru.nucleus.search.indexers.app.builders;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CodeEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  @SuppressWarnings("rawtypes")
  protected void addTaxnomy(JsonArray taxonomyArray, TaxonomyEo taxonomyEo) {
    JsonArray subjectArray = new JsonArray();
    JsonArray courseArray = new JsonArray();
    JsonArray domainArray = new JsonArray();
    JsonArray standardArray = new JsonArray();
    JsonArray learningTargetArray = new JsonArray();

    JsonArray standardDisplayArray = new JsonArray();
    JsonArray ltDisplayArray = new JsonArray();
    
    JsonArray subjectLabelArray = new JsonArray();
    JsonArray courseLabelArray = new JsonArray();
    JsonArray domainLabelArray = new JsonArray();

    JsonObject taxonomyDataSet = new JsonObject();
    JsonObject curriculumTaxonomy = new JsonObject();

    if (taxonomyArray != null && taxonomyArray.size() > 0) {
      for (int index = 0; index < taxonomyArray.size(); index++) {

        String code = taxonomyArray.getString(index);
        String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);

        String subjectCode = null;
        String courseCode = null;
        String domainCode = null;
        String standardCode = null;
        String learningTargetCode = null;

        if (codes.length > 0) {
          if (codes.length == 1) {
            subjectCode = code;
          } else if (codes.length >= 2) {
            subjectCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 1));
            if (codes.length == 2) {
              courseCode = code;
            } else if (codes.length >= 3) {
              courseCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 2));
              if (codes.length == 3) {
                domainCode = code;
              } else if (codes.length >= 4) {
                domainCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 3));
                if (codes.length == 4) {
                  standardCode = code;
                } else if (codes.length == 5) {
                  standardCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 4));
                  learningTargetCode = code;
                  learningTargetArray.add(learningTargetCode);
                  List<Map> ltData = getTaxonomyRepo().getDefaultTaxonomyData(learningTargetCode, IndexerConstants.LEARNING_TARGET);
                  if(ltData != null && ltData.size() > 0) {
                    ltDisplayArray.add(ltData.get(0).get(EntityAttributeConstants.CODE).toString());
                  }
                }
                if (standardCode != null) {
                  standardArray.add(standardCode);
                  List<Map> standardData = getTaxonomyRepo().getDefaultTaxonomyData(standardCode, IndexerConstants.STANDARD);
                  if(standardData != null && standardData.size() > 0) {
                    standardDisplayArray.add(standardData.get(0).get(EntityAttributeConstants.CODE).toString());
                  }
                }
              }
            }
          }
        }
        setTaxonomyMeta(subjectArray, courseArray, domainArray, subjectLabelArray, courseLabelArray, domainLabelArray, subjectCode, courseCode,
                domainCode);
      }
    }
    if (subjectArray.size() > 0) taxonomyEo.setSubject(new JsonArray(subjectArray.stream().distinct().collect(Collectors.toList())));
    if (courseArray.size() > 0)  taxonomyEo.setCourse(new JsonArray(courseArray.stream().distinct().collect(Collectors.toList())));
    if (domainArray.size() > 0)  taxonomyEo.setDomain(new JsonArray(domainArray.stream().distinct().collect(Collectors.toList())));
    
    JsonArray standards = null;
    JsonArray learningTarget = null;
    JsonArray standardDisplay = null;
    JsonArray learningTargetDisplay = null;
    if (standardArray.size() > 0) {
      taxonomyEo.setHasStandard(1);
      //set standard internal code and display code
      standards = new JsonArray(standardArray.stream().distinct().collect(Collectors.toList()));
      taxonomyEo.setStandards(standards);
      standardDisplay = new JsonArray(standardDisplayArray.stream().distinct().collect(Collectors.toList()));
      taxonomyEo.setStandardsDisplay(standardDisplay);
      
      //set learningTarget internal code and display code
      learningTarget = new JsonArray(learningTargetArray.stream().distinct().collect(Collectors.toList()));
      taxonomyEo.setLearningTargets(learningTarget);
      learningTargetDisplay = new JsonArray(ltDisplayArray.stream().distinct().collect(Collectors.toList()));
      taxonomyEo.setLearningTargetsDisplay(learningTargetDisplay);
    } else {
      taxonomyEo.setHasStandard(0);
    }
    taxonomyDataSet.put(IndexerConstants.SUBJECT, new JsonArray(subjectLabelArray.stream().distinct().collect(Collectors.toList())));
    taxonomyDataSet.put(IndexerConstants.COURSE, new JsonArray(courseLabelArray.stream().distinct().collect(Collectors.toList())));
    taxonomyDataSet.put(IndexerConstants.DOMAIN, new JsonArray(domainLabelArray.stream().distinct().collect(Collectors.toList())));
    curriculumTaxonomy.put(IndexerConstants.CURRICULUM_CODE, standardDisplay != null ? standardDisplay : new JsonArray())
            .put(IndexerConstants.CURRICULUM_DESC, new JsonArray())
            .put(IndexerConstants.CURRICULUM_NAME, new JsonArray());
    taxonomyDataSet.put(IndexerConstants.CURRICULUM, curriculumTaxonomy);
    taxonomyEo.setTaxonomyDataSet(taxonomyDataSet.toString());
  }

  
  @SuppressWarnings("rawtypes")
  private void setTaxonomyMeta(JsonArray subjectArray, JsonArray courseArray, JsonArray domainArray, JsonArray subjectLabelArray,
          JsonArray courseLabelArray, JsonArray domainLabelArray, String subjectCode, String courseCode, String domainCode) {
    if (subjectCode != null) {
      CodeEo subject = new CodeEo();
      List<Map> subjectData = getTaxonomyRepo().getDefaultTaxonomyData(subjectCode, IndexerConstants.SUBJECT);
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
      List<Map> courseData = getTaxonomyRepo().getDefaultTaxonomyData(courseCode, IndexerConstants.COURSE);
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
      List<Map> domainData = getTaxonomyRepo().getDefaultTaxonomyData(domainCode, IndexerConstants.DOMAIN);
      if (domainData != null && domainData.size() > 0) {
        String domainTitle = domainData.get(0).get(EntityAttributeConstants.TITLE).toString();
        domainLabelArray.add(domainTitle);
        domain.setLabel(domainTitle);
      }
      domain.setCodeId(domainCode);
      domainArray.add(domain.getCode());
    }
  }

}
