package org.gooru.nucleus.search.indexers.app.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
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

import com.fasterxml.jackson.core.type.TypeReference;

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

  private static final String QUERY = "{ \"post_filter\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"contentFormat\" : \"resource\" } }, { \"term\" : { \"publishStatus\" : \"published\" } }, { \"terms\" : { \"taxonomy.allEquivalentInternalCodes\" : [CROSSWALK_CODES] } }, { \"term\" : { \"statistics.statusIsBroken\" : 0 } } ] } }, \"size\" : 20, \"query\" : { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"description^1.5F\", \"text\", \"tags^3.0F\", \"title^5.0F\", \"narration\", \"collectionTitles\", \"originalCreator.usernameDisplay\", \"creator.usernameDisplay\", \"originalCreator.usernameDisplay.usernameDisplaySnowball\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"taxonomy.course.label^1.4F\", \"taxonomy.subject.label^1.1F\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\", \"resourceSource.attribution\", \"copyrightOwnerList.copyrightOwnerListSnowball\", \"info.publisher\", \"info.publisher.publisherSnowball\", \"copyrightOwnerList.copyrightOwnerListStandard\" ], \"boost\" : 1.0, \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"standard\" } }, \"from\" : 0, \"_source\" : [ \"id\", \"contentFormat\", \"url\", \"title\", \"description\", \"thumbnail\", \"createdAt\", \"updatedAt\", \"shortTitle\", \"narration\", \"publishStatus\", \"collectionId\", \"visibleOnProfile\", \"originalCreator\", \"creator\", \"contentSubFormat\", \"collectionIds\", \"collectionTitles\", \"question\", \"metadata\", \"taxonomy\", \"statistics\", \"license\", \"info\", \"course\", \"copyrightOwnerList\", \"isCopyrightOwner\" ], \"rescore\" : { \"window_size\" : 300, \"query\" : { \"score_mode\" : \"multiply\", \"rescore_query\" : { \"function_score\" : { \"script_score\" : { \"script\" : { \"lang\" : \"painless\", \"source\" : \"((_score/60*100) - _score) * doc['statistics.preComputedWeight'].value\" }} } } } } }";
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

  @SuppressWarnings("unchecked")
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
            cwSource.forEach(eqCompetency -> {
              JsonObject equivalentCompetency = (JsonObject) eqCompetency;
              crosswalkCodes.add(equivalentCompetency.getString(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID).toLowerCase());
            });
          }
          if (!crosswalkCodes.isEmpty()) {
            LOGGER.info("crosswalkCodes :: {} ", crosswalkCodes);
            String query = QUERY.replaceAll("CROSSWALK_CODES", convertArrayToString(StringUtils.join(crosswalkCodes, IndexerConstants.COMMA)));
            Response searchResponse = performRequest("POST","/" + IndexNameHolder.getIndexName(EsIndex.RESOURCE) + "/" + IndexerConstants.TYPE_RESOURCE + "/_search", query);
            if (searchResponse.getEntity() != null) {
              Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                      new TypeReference<Map<String, Object>>() {});
              Map<String, Object> hitsMap = (Map<String, Object>) responseAsMap.get("hits");
              Long totalHits = ((Integer) hitsMap.get("total")).longValue();
              LOGGER.debug("search count : {}", totalHits);
              if (totalHits > 0) {
                Map<String, StringBuffer> suggestByPerfAsMap = new HashMap<>();
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
                  if ((hitCount <= 3 && !(suggestIdList.size() < hitCount)) || suggestIdList.size() == 3) {
                    highSuggestIds = suggestIds;
                    LOGGER.info("hitCount : {} suggestIdList.size() : {} highSuggestIds : {} ", hitCount, suggestIdList.size(),
                            highSuggestIds.toString());
                    suggestIds = new StringBuffer();
                  } else if ((!(suggestIdList.size() > 6) && !(suggestIdList.size() < hitCount)) || (hitCount >= 6 && suggestIdList.size() == 6)) {
                    mediumSuggestIds = suggestIds;
                    LOGGER.info("hitCount : {} suggestIdList.size() : {} mediumSuggestIds : {} ", hitCount, suggestIdList.size(),
                            mediumSuggestIds.toString());
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
  
}
