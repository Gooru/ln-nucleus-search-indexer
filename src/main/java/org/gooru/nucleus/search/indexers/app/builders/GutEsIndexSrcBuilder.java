package org.gooru.nucleus.search.indexers.app.builders;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CodeEo;
import org.gooru.nucleus.search.indexers.app.index.model.CollectionEo;
import org.gooru.nucleus.search.indexers.app.index.model.GutEio;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEio;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.KeywordCard;
import org.gooru.nucleus.search.indexers.app.utils.KeywordsExtractor;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class GutEsIndexSrcBuilder<S extends JsonObject, D extends GutEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new GutEio());
  }

  @Override
  public String getName() {
    return IndexType.GUT.getType();
  }

  @SuppressWarnings({ "rawtypes" })
  @Override
  protected JsonObject build(JsonObject source, D gutEo) throws Exception {

    String gutCode = source.getString(EntityAttributeConstants.ID);
    gutEo.setId(gutCode);
    String title = source.getString(IndexFields.TITLE);
    gutEo.setTitle(source.getString(IndexFields.TITLE));
    gutEo.setCodeType(source.getString(EntityAttributeConstants.CODE_TYPE));
    gutEo.setDisplayCode(source.getString(EntityAttributeConstants.CODE));
    gutEo.setIndexType(IndexerConstants.TYPE_GUT);
    gutEo.setIndexUpdatedTime(new Date());

  //Set Competency
    JsonObject competencyObject = getTaxonomyCodeRepo().getCode(source.getString(TaxonomyCode.PARENT_TAXONOMY_CODE_ID));
    if (competencyObject != null) {
      TaxonomyEio competencyEo = new TaxonomyEio();
      competencyEo.setId(competencyObject.getString(EntityAttributeConstants.ID));
      competencyEo.setDisplayCode(competencyObject.getString(EntityAttributeConstants.CODE));
      competencyEo.setTitle(competencyObject.getString(EntityAttributeConstants.TITLE));
      competencyEo.setDescription(competencyObject.getString(EntityAttributeConstants.DESCRIPTION));
      competencyEo.setCodeType(competencyObject.getString(EntityAttributeConstants.CODE_TYPE));
      gutEo.setCompetency(competencyEo.getTaxonomyJson());
    }
    
    JsonArray gutPrerequisites = getTaxonomyRepo().getGutPrerequisites(gutCode);
    JsonArray signatureResources = getIndexRepo().getSignatureResourcesByGutCode(gutCode);
    JsonArray signatureCollections = getSignatureItemsRepo().getSignatureItemsByGutCode(gutCode, IndexerConstants.TYPE_COLLECTION);
    JsonArray signatureAsssessments = getSignatureItemsRepo().getSignatureItemsByGutCode(gutCode, IndexerConstants.TYPE_ASSESSMENT);
    gutEo.setPrerequisites(gutPrerequisites);
    gutEo.setSignatureResources(generateSignatureItems(signatureResources, IndexerConstants.TYPE_RESOURCE));
    gutEo.setSignatureCollections(generateSignatureItems(signatureCollections, IndexerConstants.TYPE_COLLECTION));
    gutEo.setSignatureAssessments(generateSignatureItems(signatureAsssessments, IndexerConstants.TYPE_ASSESSMENT));

    List<Map> equivalentCompetencyList = getTaxonomyRepo().getEquivalentCompetencies(gutCode);
    if (equivalentCompetencyList != null && !equivalentCompetencyList.isEmpty()) {
      JsonArray crosswalkCodes = new JsonArray();
      equivalentCompetencyList.forEach(eqCompetency -> {
        JsonObject crosswalkCode = setCrosswalkObject(eqCompetency);
        crosswalkCodes.add(crosswalkCode);
      });
      if (!crosswalkCodes.isEmpty())
        gutEo.setCrosswalkCodes(crosswalkCodes);
    }

    CodeEo subjectCodeEo = new CodeEo();
    JsonArray subjectLabelArray = new JsonArray();
    extractSubject(gutCode, subjectCodeEo, subjectLabelArray);
    if (subjectCodeEo.getCodeId() != null) gutEo.setSubject(subjectCodeEo.getCodeJson());
    if (!subjectLabelArray.isEmpty()) gutEo.setSubjectLabel(subjectLabelArray.getString(0));

    CodeEo courseCodeEo = new CodeEo();
    JsonArray courseLabelArray = new JsonArray();
    extractCourse(gutCode, courseCodeEo, courseLabelArray);
    if (courseCodeEo.getCodeId() != null) gutEo.setCourse(courseCodeEo.getCodeJson());
    if (!courseLabelArray.isEmpty()) gutEo.setCourseLabel(courseLabelArray.getString(0));

    CodeEo domainCodeEo = new CodeEo();
    JsonArray domainLabelArray = new JsonArray();
    extractDomain(gutCode, domainCodeEo, domainLabelArray);
    if (domainCodeEo.getCodeId() != null) gutEo.setDomain(domainCodeEo.getCodeJson());
    if (!domainLabelArray.isEmpty()) gutEo.setDomainLabel(domainLabelArray.getString(0));

    //Extract and Index keywords
    if (StringUtils.isNotBlank(title)) {
      try {
        JsonArray keywords = extractAndIndexKeywords(title);
        gutEo.setKeywords(keywords);
        gutEo.setKeywordsSuggestion(gutEo.getKeywords());
      } catch (Exception e) {
        LOGGER.info("Exception while extracting keyword from competency : {}", e.getMessage());
      }
    }
    
    return gutEo.getGutJson();
  }

  @SuppressWarnings("rawtypes")
  private void extractSubject(String code, CodeEo codeEo, JsonArray subjectLabelArray) {
    String subjectCode = null;
    String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length == 1) {
      subjectCode = code;
    } else if (codes.length > 1) {
      subjectCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 1));
    }
    List<Map> subjectData = getTaxonomyRepo().getTaxonomyData(subjectCode, IndexerConstants.SUBJECT);
    if (subjectData != null && subjectData.size() > 0) {
      String title = subjectData.get(0).get(EntityAttributeConstants.TITLE).toString();
      if (subjectLabelArray != null)
        subjectLabelArray.add(title);
      codeEo.setLabel(title);
      codeEo.setSubjectClassification(subjectData.get(0).get(EntityAttributeConstants.SUBJECT_CLASSIFICATION).toString());
    }
    codeEo.setCodeId(subjectCode);
  }

  @SuppressWarnings("rawtypes")
  private void extractCourse(String code, CodeEo codeEo, JsonArray courseLabelArray) {
    String courseCode = null;
    String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length == 2) {
      courseCode = code;
    } else if (codes.length > 2) {
      courseCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 2));
    }
    List<Map> courseData = getTaxonomyRepo().getTaxonomyData(courseCode, IndexerConstants.COURSE);
    if (courseData != null && courseData.size() > 0) {
      String title = courseData.get(0).get(EntityAttributeConstants.TITLE).toString();
      if (courseLabelArray != null)
        courseLabelArray.add(title);
      codeEo.setLabel(title);
    }
    codeEo.setCodeId(courseCode);
  }

  @SuppressWarnings("rawtypes")
  private void extractDomain(String code, CodeEo codeEo, JsonArray domainLabelArray) {
    String domainCode = null;
    String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length == 3) {
      domainCode = code;
    } else if (codes.length > 3) {
      domainCode = code.substring(0, StringUtils.ordinalIndexOf(code, "-", 3));
    }
    List<Map> domainData = getTaxonomyRepo().getTaxonomyData(domainCode, IndexerConstants.DOMAIN);
    if (domainData != null && domainData.size() > 0) {
      String title = domainData.get(0).get(EntityAttributeConstants.TITLE).toString();
      if (domainLabelArray != null)
        domainLabelArray.add(title);
      codeEo.setLabel(title);
    }
    codeEo.setCodeId(domainCode);
  }

  private JsonArray generateSignatureItems(JsonArray signatureCollections, String contentType) {
    JsonArray items = new JsonArray();
    if (signatureCollections != null) {
      signatureCollections.forEach(o -> {
        JsonObject si = (JsonObject) o;
        CollectionEo content = new CollectionEo();
        String id = null;
        if (IndexerConstants.COLLECTION_FORMATS.matcher(contentType).matches()) {
          id = si.getString(EntityAttributeConstants.ITEM_ID);
        } else if (contentType.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
          id = si.getString(EntityAttributeConstants.RESOURCE_ID);
        }
        content.setId(id);
        if (content.getId() != null) {
          JsonObject contentData = new JsonObject();
          if (IndexerConstants.COLLECTION_FORMATS.matcher(contentType).matches()) {
            contentData = getCollectionRepo().getCollectionById(content.getId());
            content.setDescription(contentData.getString(EntityAttributeConstants.LEARNING_OBJECTIVE, null));
          } else if (contentType.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
            contentData = getOriginalResourceRepo().getResourceById(content.getId());
            content.setDescription(contentData.getString(EntityAttributeConstants.DESCRIPTION, null));
          }
          content.setTitle(contentData.getString(EntityAttributeConstants.TITLE, null));
          content.setThumbnail(contentData.getString(EntityAttributeConstants.THUMBNAIL, null));
          content.setCurated(contentData.getBoolean(EntityAttributeConstants.IS_CURATED, false));
          // Set Primary Language
          Integer primaryLanguageId = contentData.getInteger(EntityAttributeConstants.PRIMARY_LANGUAGE, null);
          JsonObject primaryLanguage = getPrimaryLanguage(primaryLanguageId);
          if (primaryLanguage != null) content.setPrimaryLanguage(primaryLanguage);
          // Set Creator
          String creatorId = contentData.getString(EntityAttributeConstants.CREATOR_ID, null);
          if (creatorId != null) {
            UserEo creatorEo = new UserEo();
            JsonObject creator = getUserRepo().getUser(creatorId);
            if (creator != null && !creator.isEmpty()) {
              setUser(creator, creatorEo);
              content.setCreator(creatorEo.getUser());
            }
          }
          // Set Owner
          String ownerId = contentData.getString(EntityAttributeConstants.OWNER_ID, null);
          if (ownerId != null) {
            UserEo ownerEo = new UserEo();
            JsonObject owner = getUserRepo().getUser(ownerId);
            if (owner != null && !owner.isEmpty()) {
              setUser(owner, ownerEo);
              content.setOwner(ownerEo.getUser());
            }
          }
          // Set REEf
          Double efficacy = null;
          Double engagement = null;
          if (!contentData.isEmpty()) {
            efficacy = (Double) contentData.getValue(EntityAttributeConstants.EFFICACY);
            engagement = (Double) contentData.getValue(EntityAttributeConstants.ENGAGEMENT);
          }
          if (contentType.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) {
            content.setUrl(contentData.getString(EntityAttributeConstants.URL, null));
            content.setContentSubFormat(contentData.getString(EntityAttributeConstants.CONTENT_SUB_FORMAT, null));
          }
          content.setEfficacy(efficacy);
          content.setEngagement(engagement);
          content.setRelevance(null);
          items.add(content.getCollectionJson());
        }
      });
    }
    return items;
  }

  @SuppressWarnings("rawtypes")
  private JsonObject setCrosswalkObject(Map equivalentCompetency) {
    JsonObject eqCompetency = new JsonObject();
    eqCompetency.put(EntityAttributeConstants.ID, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID).toString());
    eqCompetency.put(EntityAttributeConstants.CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_DISPLAY_CODE).toString());
    eqCompetency.put(IndexFields.FRAMEWORK_CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID).toString());
    eqCompetency.put(EntityAttributeConstants.TITLE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TITLE).toString());
    return eqCompetency;
  }
  
  private JsonArray extractAndIndexKeywords(String title) throws IOException {
    Set<String> words = new HashSet<>();
    List<KeywordCard> keywordsList = KeywordsExtractor.getKeywordsList(title);
    keywordsList.forEach(keywordCard -> {
      words.addAll(keywordCard.getTerms());
    });
    BulkRequest bulkRequest = new BulkRequest();
    JsonArray keywords = new JsonArray();
    for (String word : words) {
      if (word.trim().length() < 3)
        continue;
      keywords.add(word);
      SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
      QueryBuilder filter = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(IndexerConstants.KEYWORD, word));
      sourceBuilder.query(filter);
      SearchResponse result = null;
      try {
        result = IndexService.instance().getDocument(IndexNameHolder.getIndexName(EsIndex.QUERY), IndexType.KEYWORD.getType(), sourceBuilder);
      } catch (Exception e) {
        LOGGER.debug("Error while searching keyword", word);
      }
      if (result != null && result.getHits() != null && result.getHits().getHits().length > 0) {
        LOGGER.debug("Keyword is already available in index !!" + word);
        continue;
      }
      String id = UUID.randomUUID().toString();
      JsonObject data = new JsonObject().put(EntityAttributeConstants.ID, id).put(IndexerConstants.KEYWORD, word);
      IndexRequest request = new IndexRequest(IndexNameHolder.getIndexName(EsIndex.QUERY), IndexType.KEYWORD.getType(), id).source(data.toString(),
              XContentType.JSON);
      bulkRequest.add(request);
    }
    if (bulkRequest.numberOfActions() > 0) {
      bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
      try {
        BulkResponse bulkResponse = getClient().bulk(bulkRequest);
        if (!bulkResponse.hasFailures()) {
          LOGGER.debug("Successfully indexed bulk keywords!");
        }
      } catch (IOException e) {
        INDEX_FAILURES_LOGGER.error("Failed Bulk index for keywords! Exception " + e.getMessage());
      }
    }
    return keywords;
  }
  

}
