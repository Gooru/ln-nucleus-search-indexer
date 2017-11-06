package org.gooru.nucleus.search.indexers.app.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ConceptBasedResourceSuggestRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PopulateConceptBasedResourceSuggestJob extends BaseIndexService implements JobInitializer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateConceptBasedResourceSuggestJob.class);
  private static final String QUERY = "{ \"post_filter\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"contentFormat\" : \"resource\" } }, { \"term\" : { \"publishStatus\" : \"published\" } }, { \"terms\" : { \"taxonomy.allEquivalentInternalCodes\" : [\"CODE\"] } }, { \"term\" : { \"statistics.statusIsBroken\" : 0 } } ] } }, \"size\" : 20, \"query\" : { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"description^1.5F\", \"text\", \"tags^3.0F\", \"title^5.0F\", \"narration\", \"collectionTitles\", \"originalCreator.usernameDisplay\", \"creator.usernameDisplay\", \"originalCreator.usernameDisplay.usernameDisplaySnowball\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"taxonomy.course.label^1.4F\", \"taxonomy.subject.label^1.1F\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\", \"resourceSource.attribution\", \"copyrightOwnerList.copyrightOwnerListSnowball\", \"info.publisher\", \"info.publisher.publisherSnowball\", \"copyrightOwnerList.copyrightOwnerListStandard\" ], \"boost\" : 1.0, \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"standard\" } }, \"from\" : 0, \"_source\" : [ \"id\", \"contentFormat\", \"url\", \"title\", \"description\", \"thumbnail\", \"createdAt\", \"updatedAt\", \"shortTitle\", \"narration\", \"publishStatus\", \"collectionId\", \"visibleOnProfile\", \"originalCreator\", \"creator\", \"contentSubFormat\", \"collectionIds\", \"collectionTitles\", \"question\", \"metadata\", \"taxonomy\", \"statistics\", \"license\", \"info\", \"course\", \"copyrightOwnerList\", \"isCopyrightOwner\" ], \"rescore\" : { \"window_size\" : 300, \"query\" : { \"score_mode\" : \"multiply\", \"rescore_query\" : { \"function_score\" : { \"script_score\" : { \"script\" : { \"lang\" : \"painless\", \"source\" : \"((_score/60*100) - _score) * doc['statistics.preComputedWeight'].value\" }} } } } } }";

  private static final Pattern STANDARD_MATCH = Pattern.compile("standard_level_1|standard_level_2");

  private static long OFFSET = 0;
  private static int BATCH_SIZE = 100;
  private static final int DAY_OF_MONTH = 17;
  private static int HOUR_OF_DAY = 13;
  private static int MINUTES = 10;

  private static class TestPopulationHolder {
    public static final PopulateConceptBasedResourceSuggestJob INSTANCE = new PopulateConceptBasedResourceSuggestJob();
  }

  public static PopulateConceptBasedResourceSuggestJob instance() {
    return TestPopulationHolder.INSTANCE;
  }
  
  @Override
  public void deployJob(JsonObject config) {
    LOGGER.info("Deploying Populate Suggestion Job....");
    JsonObject params = config.getJsonObject("populateSuggestJobSettings");

    Integer limit = params.getInteger("batchSize", BATCH_SIZE);
    Integer dayOfMonth = params.getInteger("dayOfMonth", DAY_OF_MONTH);
    Integer hourOfDay = params.getInteger("hourOfDay", HOUR_OF_DAY);
    Integer minutes = params.getInteger("minutes", MINUTES);

    MonthlyTimer scheduler = MonthlyTimer.schedule(new Runnable() {
      public void run() {
        try {
          LOGGER.info("Starting Populate Suggestion Job....");
          Long totalCountOfStdsLts = TaxonomyCodeRepository.instance().getStandardLtsCount();
          Long totalCountOfLTs = TaxonomyCodeRepository.instance().getLTCount();
          Long totalCountOfSTDs = TaxonomyCodeRepository.instance().getStandardCount();
          LOGGER.info("Total no:of codes to process : {}", totalCountOfStdsLts);
          Long expireCount = params.getLong("processCount", totalCountOfStdsLts);
          Long offset = params.getLong("offset", OFFSET);
          Long totalProcessed = 0L;
          // Process Micro-Competencies
          while (true) {
            JsonArray ltCodesArray = TaxonomyCodeRepository.instance().getLTCodeByOffset(limit, offset);
            if (ltCodesArray.size() == 0) {
              break;
            }
            //((JsonObject) ltCodesArray.getValue(0)).put("id", "NGSS.K12.SC-1-EPU-01-01");
            processTaxonomyCodes(ltCodesArray);
            totalProcessed += ltCodesArray.size();
            offset += limit;
            if (totalProcessed >= totalCountOfLTs || totalProcessed >= expireCount) {
              break;
            }
          }
          LOGGER.info("Processed all learning targets count : {}", totalCountOfLTs);

          // Process Competencies
          Long stdOffset = params.getLong("offset", OFFSET);
          while (true) {
            JsonArray standardCodesArray = TaxonomyCodeRepository.instance().getStandardCodeByOffset(limit, stdOffset);
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
        } catch (Exception e) {
          LOGGER.info("Error while populating resource suggestions : Ex ::", e);
          e.printStackTrace();
        }
      }
    }, dayOfMonth, hourOfDay, minutes);
    scheduler.cancelCurrent();
  }

  @SuppressWarnings("unchecked")
  private void processTaxonomyCodes(JsonArray taxonomyCodeArray) {
    taxonomyCodeArray.forEach(taxonomyCode -> {
      JsonObject taxonomyCodeObject = (JsonObject) taxonomyCode;
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      try {
        if (!ConceptBasedResourceSuggestRepository.instance().hasSuggestion(code)) {
          LOGGER.info("Proceed to populate, as suggestions are not present in table for code : {} ", code);
          String query = QUERY.replaceAll("CROSSWALK_CODES", code.toLowerCase());
          Response searchResponse = performRequest("POST", "/"+IndexNameHolder.getIndexName(EsIndex.RESOURCE)+"/"+IndexerConstants.TYPE_RESOURCE+"/_search", query);
          if (searchResponse.getEntity() != null) {
            Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                    new TypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> hitsMap = (Map<String, Object>) responseAsMap.get("hits");
            Long totalHits = ((Integer) hitsMap.get("total")).longValue();
            LOGGER.debug("search count : {}", totalHits);
            if (totalHits > 0) {
              Map<String, Object> suggestByPerfAsMap = new HashMap<>();
              StringBuffer suggestIds = new StringBuffer();
              StringBuffer highSuggestIds = new StringBuffer();
              StringBuffer mediumSuggestIds = new StringBuffer();
              List<String> suggestIdList = new ArrayList<>();
              long hitCount = ((Integer) hitsMap.get("total")).longValue();
              List<Map<String, Object>> hits = (List<Map<String, Object>>) (hitsMap).get("hits");
              for (Map<String, Object> hit : hits) {
                Map<String, Object> fields = (Map<String, Object>) hit.get("_source");
                String id = (String) fields.get(EntityAttributeConstants.ID);
                if (suggestIds.length() > 0) {
                  suggestIds.append(IndexerConstants.COMMA);
                }
                suggestIds.append(id);
                suggestIdList.add(id);
                if (hitCount <= 3 || suggestIdList.size() == 3) {
                  highSuggestIds = suggestIds;
                  suggestIds = new StringBuffer();
                } else if (hitCount <= 6 || suggestIdList.size() == 6) {
                  mediumSuggestIds = suggestIds;
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
              LOGGER.info("Populated suggestions for code : {} : {} ", code, suggestIds.toString());
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
    });
  }

  private void extractAndPopulateSuggestions(JsonObject taxonomyCodeObject, Map<String, Object> perfMap) {
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
      for (Entry<String, Object> perfMa : perfMap.entrySet()) {
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

}
