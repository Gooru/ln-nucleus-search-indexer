package org.gooru.nucleus.search.indexers.app.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.GutBasedResourceSuggestRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PopulateGutBasedResourceSuggestJob extends BaseIndexService implements JobInitializer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateGutBasedResourceSuggestJob.class);
  private static final String QUERY = "{ \"post_filter\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"contentFormat\" : \"resource\" } }, { \"term\" : { \"publishStatus\" : \"published\" } }, { \"terms\" : { \"taxonomy.allEquivalentInternalCodes\" : [CROSSWALK_CODES] } }, { \"term\" : { \"statistics.statusIsBroken\" : 0 } } ] } }, \"size\" : 20, \"query\" : { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"description^1.5F\", \"text\", \"tags^3.0F\", \"title^5.0F\", \"narration\", \"collectionTitles\", \"originalCreator.usernameDisplay\", \"creator.usernameDisplay\", \"originalCreator.usernameDisplay.usernameDisplaySnowball\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"taxonomy.course.label^1.4F\", \"taxonomy.subject.label^1.1F\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\", \"resourceSource.attribution\", \"copyrightOwnerList.copyrightOwnerListSnowball\", \"info.publisher\", \"info.publisher.publisherSnowball\", \"copyrightOwnerList.copyrightOwnerListStandard\" ], \"boost\" : 1.0, \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"standard\" } }, \"from\" : 0, \"_source\" : [ \"id\", \"contentFormat\", \"url\", \"title\", \"description\", \"thumbnail\", \"createdAt\", \"updatedAt\", \"shortTitle\", \"narration\", \"publishStatus\", \"collectionId\", \"visibleOnProfile\", \"originalCreator\", \"creator\", \"contentSubFormat\", \"collectionIds\", \"collectionTitles\", \"question\", \"metadata\", \"taxonomy\", \"statistics\", \"license\", \"info\", \"course\", \"copyrightOwnerList\", \"isCopyrightOwner\" ], \"rescore\" : { \"window_size\" : 300, \"query\" : { \"score_mode\" : \"multiply\", \"rescore_query\" : { \"function_score\" : { \"script_score\" : { \"script\" : { \"lang\" : \"painless\", \"source\" : \"((_score/60*100) - _score) * doc['statistics.preComputedWeight'].value\" }} } } } } }";
  private static long OFFSET = 0;
  private static int BATCH_SIZE = 100;
  private static final int DAY_OF_MONTH = 1;
  private static final int HOUR_OF_DAY = 4;
  private static final int MINUTES = 21600;

  private static class PopulatGutSuggestionHolder {
    public static final PopulateGutBasedResourceSuggestJob INSTANCE = new PopulateGutBasedResourceSuggestJob();
  }

  public static PopulateGutBasedResourceSuggestJob instance() {
    return PopulatGutSuggestionHolder.INSTANCE;
  }
  
  @Override
  public void deployJob(JsonObject config) {
    LOGGER.info("Deploying Populate Gut Suggestion Job....");
    JsonObject params = config.getJsonObject("populateResourceSuggestJobSettings");

    Integer dayOfMonth = params.getInteger("dayOfMonth", DAY_OF_MONTH);
    Integer hourOfDay = params.getInteger("hourOfDay", HOUR_OF_DAY);
    Integer minutes = params.getInteger("minutes", MINUTES);

    MonthlyTimer.schedule(new Runnable() {
      public void run() {
        try {
          long startTime = System.currentTimeMillis();
          LOGGER.info("Starting Populate Gut Suggestion Job....");
          Long totalCountOfStdsLts = TaxonomyCodeRepository.instance().getStandardLtsCountByFramework(IndexerConstants.GUT_FRAMEWORK);
          Long totalCountOfLTs = TaxonomyCodeRepository.instance().getLTCountByFramework(IndexerConstants.GUT_FRAMEWORK);
          Long totalCountOfSTDs = TaxonomyCodeRepository.instance().getStandardCountByFramework(IndexerConstants.GUT_FRAMEWORK);
          LOGGER.info("Total no:of codes to process : {}", totalCountOfStdsLts);
          Long expireCount = params.getLong("processCount", totalCountOfStdsLts);
          Integer limit = params.getInteger("batchSize", BATCH_SIZE);
          Long offset = params.getLong("offset", OFFSET);
          Long totalProcessed = 0L;
          while (true) {
            JsonArray ltCodesArray = TaxonomyCodeRepository.instance().getLTCodeByFrameworkAndOffset(IndexerConstants.GUT_FRAMEWORK, limit, offset);
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
            JsonArray standardCodesArray = TaxonomyCodeRepository.instance().getStandardCodeByFrameworkAndOffset(IndexerConstants.GUT_FRAMEWORK, limit, stdOffset);
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
      }
    }, dayOfMonth, hourOfDay, minutes);
    //scheduler.cancelCurrent();
  }

  @SuppressWarnings("unchecked")
  private void processTaxonomyCodes(JsonArray taxonomyCodeArray) {
    for (Object taxonomyCode : taxonomyCodeArray) {
      JsonObject taxonomyCodeObject = (JsonObject) taxonomyCode;
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      try {
        if (!GutBasedResourceSuggestRepository.instance().hasSuggestion(code)) {
          LOGGER.info("Proceed to populate, as suggestions are not present in table for code : {} ", code);

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
            String query = QUERY.replaceAll("CROSSWALK_CODES", convertArrayToString(StringUtils.join(crosswalkCodes, IndexerConstants.COMMA)));
            Response searchResponse = performRequest("POST", "/"+IndexNameHolder.getIndexName(EsIndex.RESOURCE)+"/"+IndexerConstants.TYPE_RESOURCE+"/_search", query);
            if (searchResponse.getEntity() != null) {
              Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                      new TypeReference<Map<String, Object>>() {});
              Map<String, Object> hitsMap = (Map<String, Object>) responseAsMap.get("hits");
              Long totalHits = ((Integer) hitsMap.get("total")).longValue();
              LOGGER.debug("search count : {}", totalHits);
              if (totalHits > 0) {
                Map<String, StringBuffer> suggestByPerfAsMap = new HashMap<>();
                deserializeResponseAndPopulate(taxonomyCodeObject, code, hitsMap, suggestByPerfAsMap);
                LOGGER.info("Populated suggestions for code : {} : {} ", code, suggestByPerfAsMap.toString());
                if (!suggestByPerfAsMap.isEmpty()) {
                  extractAndPopulateSuggestions(taxonomyCodeObject, suggestByPerfAsMap);
                }
              } else {
                LOGGER.info("No matching suggestions for cw codes : {} ", crosswalkCodes);
              }
            }
          } else {
            LOGGER.info("No mapping for gut : {} ", code);
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

  @SuppressWarnings("unchecked")
  private void deserializeResponseAndPopulate(JsonObject taxonomyCodeObject, String code, Map<String, Object> hitsMap, Map<String, StringBuffer> suggestByPerfAsMap) {
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
        suggestIds = new StringBuffer();
      } else if ((!(suggestIdList.size() > 6) && !(suggestIdList.size() < hitCount)) || (hitCount >= 6 && suggestIdList.size() == 6)) {
        mediumSuggestIds = suggestIds;
        suggestIds = new StringBuffer();
      }
      if (hitCount >= 9 && suggestIdList.size() == 9) {
        break;
      }
    }
    if (highSuggestIds.length() > 0)
      suggestByPerfAsMap.put(IndexerConstants.ABOVE_AVERAGE, highSuggestIds);
    if (mediumSuggestIds.length() > 0)
      suggestByPerfAsMap.put(IndexerConstants.AVERAGE, mediumSuggestIds);
    if (suggestIds.length() > 0)
      suggestByPerfAsMap.put(IndexerConstants.BELOW_AVERAGE, suggestIds);
    
  }

  private void extractAndPopulateSuggestions(JsonObject taxonomyCodeObject, Map<String, StringBuffer> perfMap) {
    if (!perfMap.isEmpty()) {
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      String displayCode = taxonomyCodeObject.getString(EntityAttributeConstants.CODE);
      String codeType = taxonomyCodeObject.getString(EntityAttributeConstants.CODE_TYPE);
      String competency = null;
      String mcCompetency = null;
      String mcCompetencyDisplayCode = null;
      if (IndexerConstants.STANDARD_MATCH.matcher(codeType).matches()) {
        competency = code;
      } else if (codeType.equalsIgnoreCase(IndexerConstants.LEARNING_TARGET_TYPE_0)) {
        mcCompetency = code;
        mcCompetencyDisplayCode = displayCode;
        competency = taxonomyCodeObject.getString(TaxonomyCode.PARENT_TAXONOMY_CODE_ID);
      }
      for (Entry<String, StringBuffer> perfMapEntry : perfMap.entrySet()) {
        JsonObject suggestJson = new JsonObject();
        suggestJson.put(EntityAttributeConstants.COMPETENCY_INTERNAL_CODE, competency);
        suggestJson.put(EntityAttributeConstants.COMPETENCY_DISPLAY_CODE, displayCode);
        if (mcCompetency != null)
          suggestJson.put(EntityAttributeConstants.MICRO_COMPETENCY_INTERNAL_CODE, mcCompetency);
        suggestJson.put(EntityAttributeConstants.MICRO_COMPETENCY_DISPLAY_CODE, mcCompetencyDisplayCode);
        suggestJson.put(EntityAttributeConstants.PERFORMANCE_RANGE, perfMapEntry.getKey());
        suggestJson.put(EntityAttributeConstants.IDS_TO_SUGGEST, perfMapEntry.getValue());
        GutBasedResourceSuggestRepository.instance().saveSuggestions(code, suggestJson);
      }
    }
  }
  
}
