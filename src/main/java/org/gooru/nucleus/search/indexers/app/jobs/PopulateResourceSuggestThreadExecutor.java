package org.gooru.nucleus.search.indexers.app.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.rescore.RescoreBuilder;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ConceptBasedResourceSuggestRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PopulateResourceSuggestThreadExecutor extends BaseIndexService implements Runnable {

  private JsonObject params;

  public PopulateResourceSuggestThreadExecutor() {
  }
  public PopulateResourceSuggestThreadExecutor(JsonObject params) {
    this.params = params;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateResourceSuggestThreadExecutor.class);

  private static final String GUT_FRAMEWORK = "GDT";

  private static final String QUERY =
          "{ \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"description^1.5F\", \"text\", \"tags^3.0F\", \"title^5.0F\", \"narration\", \"collectionTitles\", \"originalCreator.usernameDisplay\", \"creator.usernameDisplay\", \"originalCreator.usernameDisplay.usernameDisplaySnowball\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"taxonomy.course.label^1.4F\", \"taxonomy.subject.label^1.1F\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\", \"resourceSource.attribution\", \"copyrightOwnerList.copyrightOwnerListSnowball\", \"info.publisher\", \"info.publisher.publisherSnowball\", \"copyrightOwnerList.copyrightOwnerListStandard\" ], \"boost\" : 1.0, \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"standard\" } }";
  private static final String FILTER = "{ \"bool\" : { \"must\" : [ { \"term\" : { \"contentFormat\" : \"resource\" } }, { \"term\" : { \"publishStatus\" : \"published\" } }, { \"terms\" : { \"taxonomy.allEquivalentInternalCodes\" : [ CODE ] } }, { \"term\" : { \"statistics.statusIsBroken\" : 0 } } ] } }";
  private static final String RESCORE_SCRIPT = "esScore = esScore=_score.score(); pcw = doc['statistics.preComputedWeight'].value; cenPrecent = esScore/60*100; fourtyPrecent = cenPrecent - esScore; return fourtyPrecent * pcw;";
  private static final Pattern STANDARD_MATCH = Pattern.compile("standard_level_1|standard_level_2");

  @Override
  public void run() {
    Integer limit = params.getInteger("batchSize", 10);
    try {
      try {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Starting Populate Gut Suggestion Job....");
        Long totalCountOfStdsLts = TaxonomyCodeRepository.instance().getStandardLtsCountByFramework(GUT_FRAMEWORK);
        Long totalCountOfLTs = TaxonomyCodeRepository.instance().getLTCountByFramework(GUT_FRAMEWORK);
        Long totalCountOfSTDs = TaxonomyCodeRepository.instance().getStandardCountByFramework(GUT_FRAMEWORK);
        LOGGER.info("Total no:of codes to process : {}", totalCountOfStdsLts);
        Long expireCount = params.getLong("processCount", totalCountOfStdsLts);
        Long offset = params.getLong("offset", 0L);
        Long totalProcessed = 0L;
        while (true) {
          JsonArray ltCodesArray = TaxonomyCodeRepository.instance().getLTCodeByFrameworkAndOffset(GUT_FRAMEWORK, limit, offset);
          if (ltCodesArray.size() == 0) {
            break;
          }
          processTaxonomyCodes(ltCodesArray);
          totalProcessed += ltCodesArray.size();
          offset += limit;
          if (totalProcessed >= totalCountOfLTs || totalProcessed >= expireCount) {
            break;
          }
        }
        LOGGER.info("Processed all learning targets count : {}", totalCountOfLTs);

        Long stdOffset = params.getLong("offset", 0L);
        while (true) {
          JsonArray standardCodesArray = TaxonomyCodeRepository.instance().getStandardCodeByFrameworkAndOffset(GUT_FRAMEWORK, limit, stdOffset);
          if (standardCodesArray.size() == 0) {
            break;
          }
          processTaxonomyCodes(standardCodesArray);
          totalProcessed += standardCodesArray.size();
          stdOffset += limit;
          if (totalProcessed >= totalCountOfStdsLts || totalProcessed >= expireCount) {
            break;
          }
        }
        LOGGER.info("Processed all standards count : {}", totalCountOfSTDs);
        LOGGER.info("Total processed std and lt codes : {} ", totalProcessed);
        LOGGER.info("Total time taken to populate : {}", (System.currentTimeMillis() - startTime));
      } catch (Exception e) {
        LOGGER.info("Error while populating resource suggestions : Ex ::", e);
        e.printStackTrace();
      }
    } catch (Exception e) {
      LOGGER.info("Error while populating resource suggestions : Ex ::", e);
      e.printStackTrace();
    }
  }

  private void processTaxonomyCodes(JsonArray taxonomyCodeArray) {
    for (Object taxonomyCode : taxonomyCodeArray) {
      JsonObject taxonomyCodeObject = (JsonObject) taxonomyCode;
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      try {
        if (!ConceptBasedResourceSuggestRepository.instance().hasSuggestion(code)) {
          LOGGER.info("No suggestions available in table for {} ", code);

          ProcessorContext context = new ProcessorContext(code, ExecuteOperationConstants.GET_CROSSWALK);
          JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
          if (result == null) {
            continue;
          }
          Set<String> crosswalkCodes = new HashSet<>();
          JsonArray cwSource = result.getJsonArray(IndexerConstants.TYPE_CROSSWALK);
          if (cwSource != null) {
            cwSource.stream().forEach(eqCompetency -> {
              JsonObject equivalentCompetency = (JsonObject) eqCompetency;
              crosswalkCodes.add(equivalentCompetency.getString(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID).toLowerCase());
            });
          }
          if (!crosswalkCodes.isEmpty()) {
            LOGGER.info("crosswalkCodes :: {} ", crosswalkCodes);
            BoolQueryBuilder filter = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("contentFormat", "resource"))
                    .must(QueryBuilders.termQuery("publishStatus", "published"))
                    .must(QueryBuilders.termQuery("statistics.statusIsBroken", 0))
                    .must(QueryBuilders.termsQuery("taxonomy.allEquivalentInternalCodes", crosswalkCodes.toArray()));
            SearchRequestBuilder requestBuilder = getClient().prepareSearch(IndexNameHolder.getIndexName(EsIndex.RESOURCE))
                    .setTypes(IndexerConstants.TYPE_RESOURCE)
                    .setQuery(QueryBuilders.queryStringQuery("*").field("_all").field("description", 1.5F).field("text").field("tags",3.0F).field("title", 5.0F)
                            .boost(1.0F).useDisMax(true).defaultOperator(Operator.AND).allowLeadingWildcard(false).analyzer("standard"))
                    .setPostFilter(filter).setSize(20).setFrom(0)
                    .setRescorer(RescoreBuilder.queryRescorer(QueryBuilders.functionScoreQuery(
                            ScoreFunctionBuilders.scriptFunction(new Script("((_score/60*100) - _score) * doc['statistics.preComputedWeight'].value")))), 300)
                    .setFetchSource(IndexFields.ID, null);
           SearchResponse searchResponse = requestBuilder.execute().actionGet();
            if (searchResponse.getHits().getTotalHits() > 0) {
              LOGGER.debug("search count : {}", searchResponse.getHits().getTotalHits());

              Map<String, StringBuffer> suggestByPerfAsMap = new HashMap<>();
              StringBuffer suggestIds = new StringBuffer();
              StringBuffer highSuggestIds = new StringBuffer();
              StringBuffer mediumSuggestIds = new StringBuffer();
              List<String> suggestIdList = new ArrayList<>();
              long hitCount = searchResponse.getHits().getTotalHits();
              for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                String id = searchHit.getId();
                if (suggestIds.length() > 0) {
                  suggestIds.append(IndexerConstants.COMMA);
                }
                suggestIds.append(id);
                suggestIdList.add(id);
                if ((hitCount <= 3 && !(suggestIdList.size() < hitCount)) || suggestIdList.size() == 3) {
                  highSuggestIds = suggestIds;
                  LOGGER.info("hitCount : {} suggestIdList.size() : {} highSuggestIds : {} ", hitCount, suggestIdList.size(), highSuggestIds.toString());
                  suggestIds = new StringBuffer();
                } else if ((!(suggestIdList.size() > 6) && !(suggestIdList.size() < hitCount)) || (hitCount >= 6 && suggestIdList.size() == 6)) {
                  mediumSuggestIds = suggestIds;
                  LOGGER.info("hitCount : {} suggestIdList.size() : {} mediumSuggestIds : {} ", hitCount, suggestIdList.size(), mediumSuggestIds.toString());
                  suggestIds = new StringBuffer();
                }
                if (hitCount >= 9 && suggestIdList.size() == 9) {
                  break;
                }
              }
              if (highSuggestIds.length() > 0)
                suggestByPerfAsMap.put("H", highSuggestIds);
              if (mediumSuggestIds.length() > 0)
                suggestByPerfAsMap.put("M", mediumSuggestIds);
              if (suggestIds.length() > 0)
                suggestByPerfAsMap.put("L", suggestIds);
              LOGGER.info("Populated suggestions for code : {} : {} ", code, suggestByPerfAsMap.toString());
              if (!suggestByPerfAsMap.isEmpty()) {
                extractAndPopulateSuggestions(taxonomyCodeObject, suggestByPerfAsMap);
              }
            }
          }
        } else {
          LOGGER.info("Already suggestions are populated for this code : {} ", code);
        }
      } catch (Exception e) {
        LOGGER.info("Error while checking or populating suggestions : {}, Exception : {}", code, e);
        e.printStackTrace();
      }
    }
  }

  private void extractAndPopulateSuggestions(JsonObject taxonomyCodeObject, Map<String, StringBuffer> perfMap) {
    if (!perfMap.isEmpty()) {
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      String codeType = taxonomyCodeObject.getString("code_type");
      String competency = null;
      String mcCompetency = null;
      LOGGER.info("CODETYPE : {}", codeType);
      if (STANDARD_MATCH.matcher(codeType).matches()) {
        competency = code;
      } else if (codeType.equalsIgnoreCase("learning_target_level_0")) {
        mcCompetency = code;
        competency = taxonomyCodeObject.getString(TaxonomyCode.PARENT_TAXONOMY_CODE_ID);
      }
      for (Entry<String, StringBuffer> perfMa : perfMap.entrySet()) {
        JsonObject suggestJson = new JsonObject();
        suggestJson.put("competency_internal_code", competency);
        if (mcCompetency != null)
          suggestJson.put("micro_competency_internal_code", mcCompetency);
        suggestJson.put("ctx_type", "collection-study");
        suggestJson.put("suggest_type", "resource");
        suggestJson.put("performance_range", perfMa.getKey());
        suggestJson.put("ids_to_suggest", perfMa.getValue());
        ConceptBasedResourceSuggestRepository.instance().saveResourceSuggest(code, suggestJson);
      }
    }
  }
  
  public static void main(String a[]) {
    StringBuffer suggestIds = new StringBuffer();
    List<String> suggestIdList = new ArrayList<>();
    List<String> hits = new ArrayList<>();
    hits.add("a");
    hits.add("b");
    hits.add("c");
    hits.add("d");
    hits.add("e");
    hits.add("f");
    hits.add("g");
    hits.add("h");
    
    long hitCount = hits.size();
    Map<String, StringBuffer> suggestByPerfAsMap = new HashMap<>();
    StringBuffer highSuggestIds = new StringBuffer();
    StringBuffer mediumSuggestIds = new StringBuffer();
    for (String hit : hits) {
      suggestIdList.add(hit);
      if (suggestIds.length() > 0) {
        suggestIds.append(IndexerConstants.COMMA);
      }
      suggestIds.append(hit);
      if ((hitCount <= 3 && !(suggestIdList.size() < hitCount)) || suggestIdList.size() == 3) {
        System.out.println("hitCount : "+hitCount+" suggestIdList.size() : "+suggestIdList.size());
        //highProcessed = true;
        highSuggestIds = suggestIds;
        suggestIds = new StringBuffer();
      } else if (((!(suggestIdList.size() > 6) && !(suggestIdList.size() < hitCount)) || (hitCount >= 6 && suggestIdList.size() == 6))) {
        mediumSuggestIds = suggestIds;
        System.out.println("hitCount : "+hitCount+" msuggestIdList.size() : "+suggestIdList.size());
        suggestIds = new StringBuffer();
      }
      if (hitCount >= 9 && suggestIdList.size() == 9) {
        break;
      }
    }
    if (highSuggestIds.length() > 0)
      suggestByPerfAsMap.put("H", highSuggestIds);
    if (mediumSuggestIds.length() > 0)
      suggestByPerfAsMap.put("M", mediumSuggestIds);
    if (suggestIds.length() > 0)
      suggestByPerfAsMap.put("L", suggestIds);
    System.out.println("Populated suggestions for code : " +suggestByPerfAsMap.toString());
  }
}
