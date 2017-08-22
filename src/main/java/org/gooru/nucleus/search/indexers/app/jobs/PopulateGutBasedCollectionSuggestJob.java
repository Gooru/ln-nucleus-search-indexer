package org.gooru.nucleus.search.indexers.app.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.GutBasedCollectionSuggestRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PopulateGutBasedCollectionSuggestJob extends BaseIndexService implements JobInitializer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateGutBasedCollectionSuggestJob.class);
  
  private static final String QUERY =
          "{ \"function_score\" : { \"script_score\" : { \"lang\" : \"groovy\", \"script\" : \"1.0\" }, \"query\" : { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"taxonomyDataSet\", \"learningObjective\", \"originalCreator.usernameDisplay\", \"originalCreator.usernameDisplay.usernameDisplaySnowball\", \"owner.usernameDisplay\", \"creator.usernameDisplay\", \"owner.usernameDisplay.usernameDisplaySnowball\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"title.titleKStem^10.0F\", \"taxonomy.subject.label^1.1F\", \"taxonomy.course.label^1.4F\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\", \"resourceTitles\", \"collectionContents.description\", \"creator.usernameDisplay.usernameDisplayKStem\", \"owner.usernameDisplay.usernameDisplayKStem\", \"originalCreator.usernameDisplay.usernameDisplayKStem\" ], \"boost\" : 3.0, \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"gooru_kstem\" } } } }";
  private static final String FILTER =
          "{ \"bool\" : { \"must\" : [ { \"term\" : { \"contentFormat\" : \"collection\" } }, { \"term\" : { \"publishStatus\" : \"published\" } }, { \"terms\" : { \"taxonomy.allEquivalentInternalCodes\" : [ CROSSWALK_CODES ] } }, { \"bool\" : { \"mustNot\" : [ { \"term\" : { \"contentSubFormat\" : \"benchmark\" } } ] } } ] } }";

  private static long OFFSET = 0;
  private static int BATCH_SIZE = 100;
  private static final int DAY_OF_MONTH = 1;
  private static final int HOUR_OF_DAY = 4;
  private static final int MINUTES = 21600;

  private static class PopulatGutSuggestionHolder {
    public static final PopulateGutBasedCollectionSuggestJob INSTANCE = new PopulateGutBasedCollectionSuggestJob();
  }

  public static PopulateGutBasedCollectionSuggestJob instance() {
    return PopulatGutSuggestionHolder.INSTANCE;
  }
  
  @Override
  public void deployJob(JsonObject config) {
    LOGGER.info("Deploying Populate Gut Suggestion Job....");
    JsonObject params = config.getJsonObject("populateCollectionSuggestJobSettings");

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

  private void processTaxonomyCodes(JsonArray taxonomyCodeArray) {
    for (Object taxonomyCode : taxonomyCodeArray) {
      JsonObject taxonomyCodeObject = (JsonObject) taxonomyCode;
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      try {
        if (!GutBasedCollectionSuggestRepository.instance().hasSuggestion(code)) {
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
            SearchRequestBuilder requestBuilder = getClient().prepareSearch(IndexNameHolder.getIndexName(EsIndex.COLLECTION))
                    .setTypes(IndexerConstants.TYPE_COLLECTION).setQuery(QUERY)
                    .setPostFilter(FILTER.replace(IndexerConstants.CAPS_CROSSWALK_CODES, convertArrayToString(StringUtils.join(crosswalkCodes, ",")))).setSize(20).setFrom(0)
                    .setFetchSource("id", null);
            SearchResponse searchResponse = requestBuilder.execute().actionGet();
            if (searchResponse.getHits().getTotalHits() > 0) {
              Map<String, StringBuffer> suggestByPerfAsMap = new HashMap<>();
              deserializeResponseAndPopulate(taxonomyCodeObject, code, searchResponse, suggestByPerfAsMap);
              LOGGER.info("Populated suggestions for code : {} : {} ", code, suggestByPerfAsMap.toString());
              if (!suggestByPerfAsMap.isEmpty()) {
                extractAndPopulateSuggestions(taxonomyCodeObject, suggestByPerfAsMap);
              }
            } else {
              LOGGER.info("No matching suggestions for cw codes : {} ", crosswalkCodes);
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

  private void deserializeResponseAndPopulate(JsonObject taxonomyCodeObject, String code, SearchResponse searchResponse, Map<String, StringBuffer> suggestByPerfAsMap) {
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
        GutBasedCollectionSuggestRepository.instance().saveSuggestions(code, suggestJson);
      }
    }
  }

}
