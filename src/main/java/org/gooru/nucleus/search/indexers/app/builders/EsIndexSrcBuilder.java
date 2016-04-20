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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void setUser(Map orginalCreator, UserEo userEo) {
    userEo.setUsername(BaseUtil.checkNullAndGetString(orginalCreator, "username"));
    userEo.setUsernameDisplay(BaseUtil.checkNullAndGetString(orginalCreator, "username"));
    userEo.setUserId(BaseUtil.checkNullAndGetString(orginalCreator, "id"));
    userEo.setLastName(BaseUtil.checkNullAndGetString(orginalCreator, "lastname"));
    userEo.setFirstName(BaseUtil.checkNullAndGetString(orginalCreator, "firstname"));
    userEo.setFullName(
            BaseUtil.checkNullAndGetString(orginalCreator, "firstname") + ' ' + BaseUtil.checkNullAndGetString(orginalCreator, "lastname"));
    userEo.setEmailId(BaseUtil.checkNullAndGetString(orginalCreator, "email_id"));
    if (orginalCreator.get("metadata") != null) {
      JsonObject metadata = new JsonObject(orginalCreator.get("metadata").toString());
      userEo.setProfileVisibility(metadata.getBoolean("is_profile_visible", false));
    }
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

    for (int index = 0; index < taxonomyArray.size(); index++) {
      CodeEo subject = new CodeEo();
      CodeEo course = new CodeEo();
      CodeEo domain = new CodeEo();

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
            } else {
              domainCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 3));
            }
          }
        }
      }

      List<Map> subjectData = getTaxonomyRepo().getDefaultTaxonomyData(subjectCode, IndexerConstants.SUBJECT);
      String subjectTitle = (subjectData != null && subjectData.size() > 0) ? subjectData.get(0).get(EntityAttributeConstants.TITLE).toString() : "";
      List<Map> courseData = getTaxonomyRepo().getDefaultTaxonomyData(courseCode, IndexerConstants.COURSE);
      String courseTitle = (courseData != null && courseData.size() > 0) ? courseData.get(0).get(EntityAttributeConstants.TITLE).toString() : "";
      List<Map> domainData = getTaxonomyRepo().getDefaultTaxonomyData(domainCode, IndexerConstants.DOMAIN);
      String domainTitle = (domainData != null && domainData.size() > 0) ? domainData.get(0).get(EntityAttributeConstants.TITLE).toString() : "";

      if (subjectCode != null) {
        subject.setCodeId(subjectCode);
        subject.setLabel(subjectTitle);
      }
      if (courseCode != null) {
        course.setCodeId(courseCode);
        course.setLabel(courseTitle);
      }
      if (domainCode != null) {
        domain.setCodeId(domainCode);
        domain.setLabel(domainTitle);
      }
      if(!subject.getCode().isEmpty()) subjectArray.add(subject.getCode());
      if(!course.getCode().isEmpty()) courseArray.add(course.getCode());
      if(!domain.getCode().isEmpty()) domainArray.add(domain.getCode());
      subjectLabelArray.add(subjectTitle);
      courseLabelArray.add(courseTitle);
      domainLabelArray.add(domainTitle);

      if (codes.length >= 4) {
        if (codes.length == 4) {
          standardCode = code;
        } else if (codes.length == 5) {
          standardCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 4));
          learningTargetCode = code;
        }
        if (standardCode != null) {
          standardArray.add(standardCode);
          List<Map> standardData = getTaxonomyRepo().getDefaultTaxonomyData(standardCode, IndexerConstants.STANDARD);
          standardDisplayArray.add((standardData != null && standardData.size() > 0) ? standardData.get(0).get(EntityAttributeConstants.CODE).toString() : "");
        }
        if (learningTargetCode != null) {
          learningTargetArray.add(learningTargetCode);
          List<Map> ltData = getTaxonomyRepo().getDefaultTaxonomyData(learningTargetCode, IndexerConstants.LEARNING_TARGET);
          ltDisplayArray.add((ltData != null && ltData.size() > 0) ? ltData.get(0).get(EntityAttributeConstants.CODE).toString() : "");
        }
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

}
