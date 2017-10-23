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
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.DomainEo;
import org.gooru.nucleus.search.indexers.app.index.model.SubjectEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEio;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.app.utils.KeywordCard;
import org.gooru.nucleus.search.indexers.app.utils.KeywordsExtractor;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TaxonomyEsIndexSrcBuilder<S extends JsonObject, D extends TaxonomyEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new TaxonomyEio());
  }

  @Override
  public String getName() {
    return IndexType.TAXONOMY.getType();
  }

  @SuppressWarnings({ "rawtypes" })
  @Override
  protected JsonObject build(JsonObject source, D taxonomyEo) throws Exception {
    String codeId = source.getString(TaxonomyCode.ID);
    taxonomyEo.setId(codeId);
    taxonomyEo.setIndexType(IndexerConstants.TYPE_TAXONOMY);
    taxonomyEo.setDisplayCode(source.getString(EntityAttributeConstants.CODE));
    String title = source.getString(EntityAttributeConstants.TITLE);
    taxonomyEo.setTitle(title);
    taxonomyEo.setDescription(source.getString(EntityAttributeConstants.DESCRIPTION));
    taxonomyEo.setCodeType(source.getString(EntityAttributeConstants.CODE_TYPE));
    taxonomyEo.setIndexUpdatedTime(new Date());

    //Set Competency
    JsonObject competencyObject = getTaxonomyCodeRepo().getCode(source.getString(TaxonomyCode.PARENT_TAXONOMY_CODE_ID));
    if (competencyObject != null) {
      TaxonomyEio competencyEo = new TaxonomyEio();
      competencyEo.setId(competencyObject.getString(EntityAttributeConstants.ID));
      competencyEo.setDisplayCode(competencyObject.getString(EntityAttributeConstants.CODE));
      competencyEo.setTitle(competencyObject.getString(EntityAttributeConstants.TITLE));
      competencyEo.setDescription(competencyObject.getString(EntityAttributeConstants.DESCRIPTION));
      competencyEo.setCodeType(competencyObject.getString(EntityAttributeConstants.CODE_TYPE));
      taxonomyEo.setCompetency(competencyEo.getTaxonomyJson());
    }
    
    //Set Crosswalk
    JsonObject gutCodeObject = getTaxonomyRepo().getGDTCode(codeId);
    if (gutCodeObject != null && !gutCodeObject.isEmpty()) {
      String gutCode = gutCodeObject.getString(TaxonomyCodeMapping.SOURCE_TAXONOMY_CODE_ID);
      List<Map> equivalentCompetencyList = getTaxonomyRepo().getEquivalentCompetencies(gutCode);
      if (equivalentCompetencyList != null && !equivalentCompetencyList.isEmpty()) {
        JsonArray crosswalkCodes = new JsonArray();
        equivalentCompetencyList.forEach(eqCompetency -> {
          if (!eqCompetency.get(TaxonomyCode.TARGET_TAXONOMY_CODE_ID).toString().equalsIgnoreCase(codeId)) {
            JsonObject crosswalkCode = setCrosswalkObject(eqCompetency);
            crosswalkCodes.add(crosswalkCode);
          }
        });
        if (!crosswalkCodes.isEmpty()) taxonomyEo.setCrosswalkCodes(crosswalkCodes);
      }
      taxonomyEo.setGutCode(gutCode);
    }
    
    String subjectCode = null;
    String courseCode = null;
    String domainCode = null;
    String[] codes = codeId.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length > 0) {
      if (codes.length == 1) {
        subjectCode = codeId;
      } else if (codes.length > 1) {
        subjectCode = codeId.substring(0, StringUtils.ordinalIndexOf(codeId, "-", 1));
      }
      if (codes.length == 2) {
        courseCode = codeId;
      } else if (codes.length > 2) {
        courseCode = codeId.substring(0, StringUtils.ordinalIndexOf(codeId, "-", 2));
      }
      if (codes.length == 3) {
        domainCode = codeId;
      } else if (codes.length > 3) {
        domainCode = codeId.substring(0, StringUtils.ordinalIndexOf(codeId, "-", 3));
      }
    }
    if (subjectCode != null) {
      SubjectEo subject = new SubjectEo();
      List<Map> subjectData = getTaxonomyRepo().getTaxonomyData(subjectCode, IndexerConstants.SUBJECT);
      if (subjectData != null && subjectData.size() > 0) {
        subject.setTitle(subjectData.get(0).get(EntityAttributeConstants.TITLE).toString());
        subject.setSubjectClassification(subjectData.get(0).get(EntityAttributeConstants.SUBJECT_CLASSIFICATION).toString());
      }
      subject.setId(subjectCode);
      taxonomyEo.setSubject(subject.getSubjectJson());
    }
    if (courseCode != null) {
      CourseEo course = new CourseEo();
      List<Map> courseData = getTaxonomyRepo().getTaxonomyData(courseCode, IndexerConstants.COURSE);
      if (courseData != null && courseData.size() > 0) {
        String courseTitle = courseData.get(0).get(EntityAttributeConstants.TITLE).toString();
        course.setTitle(courseTitle);
      }
      course.setId(courseCode);
      taxonomyEo.setCourse(course.getCourseJson());
    }
    if (domainCode != null) {
      DomainEo domain = new DomainEo();
      List<Map> domainData = getTaxonomyRepo().getTaxonomyData(domainCode, IndexerConstants.DOMAIN);
      if (domainData != null && domainData.size() > 0) {
        String domainTitle = domainData.get(0).get(EntityAttributeConstants.TITLE).toString();
        domain.setTitle(domainTitle);
      }
      domain.setId(domainCode);
      taxonomyEo.setDomain(domain.getDomainJson());
    }
    
    //Extract and Index keywords
    if (StringUtils.isNotBlank(title)) {
      try {
        JsonArray keywords = extractAndIndexKeywords(title);
        taxonomyEo.setKeywords(keywords);
        taxonomyEo.setKeywordsSuggestion(taxonomyEo.getKeywords());
      } catch (Exception e) {
        LOGGER.info("Exception while extracting keyword from competency : {}", e.getMessage());
      }
    }
    
    //TODO taxonomyEo.setGrade();
    
    return taxonomyEo.getTaxonomyJson();
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
        keywords.add(word);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        QueryBuilder filter = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(IndexerConstants.KEYWORD, word));
        sourceBuilder.query(filter);
        SearchResponse result = null;
        try {
          result = IndexService.instance().getDocument(IndexNameHolder.getIndexName(EsIndex.QUERY), IndexType.KEYWORD.getType(), sourceBuilder);
        } catch (Exception e) {
          LOGGER.debug("Error while searching keyword" , word);
        }
        if (result != null && result.getHits() != null && result.getHits().getHits().length > 0) {
          LOGGER.debug("Keyword is already available in index !!" + word);
          continue;
        }
        String id = UUID.randomUUID().toString();
        JsonObject data = new JsonObject().put(EntityAttributeConstants.ID, id).put(IndexerConstants.KEYWORD, word);
        IndexRequest request = new IndexRequest(IndexNameHolder.getIndexName(EsIndex.QUERY), IndexType.KEYWORD.getType(), id).source(data.toString(), XContentType.JSON); 
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
  
  @SuppressWarnings("rawtypes")
  private JsonObject setCrosswalkObject(Map equivalentCompetency) {
    JsonObject eqCompetency = new JsonObject(); 
    eqCompetency.put(EntityAttributeConstants.ID, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID).toString());
    eqCompetency.put(EntityAttributeConstants.CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_DISPLAY_CODE).toString());
    eqCompetency.put(IndexFields.FRAMEWORK_CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID).toString());
    eqCompetency.put(EntityAttributeConstants.TITLE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TITLE).toString());
    return eqCompetency;
  }

}
