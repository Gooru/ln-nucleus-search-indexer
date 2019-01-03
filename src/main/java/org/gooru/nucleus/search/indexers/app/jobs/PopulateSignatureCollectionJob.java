package org.gooru.nucleus.search.indexers.app.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexTrackerRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.SignatureItemsRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.IndexerJobStatus;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PopulateSignatureCollectionJob extends BaseIndexService implements JobInitializer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateSignatureCollectionJob.class);
  private static final String QUERY = "{ \"post_filter\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"publishStatus\" : \"published\" } }, { \"term\" : { \"primaryLanguage.id\" : LANG_ID } }, { \"term\" : { \"contentFormat\" : \"collection\" } }, { \"bool\" : { \"mustNot\" : [ { \"term\" : { \"contentSubFormat\" : \"benchmark\" } } ] } }, { \"terms\" : { \"taxonomy.gutCodes\" : [GUT_CODE] } } ] } }, \"size\" : 20, \"query\" : { \"function_score\" : { \"script_score\" : { \"script\" : { \"lang\" : \"painless\", \"source\" : \"_score * doc['statistics.preComputedWeight'].value\" }}, \"query\" : { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"taxonomyDataSet\", \"learningObjective\", \"originalCreator.usernameDisplay\", \"originalCreator.usernameDisplay.usernameDisplaySnowball\", \"owner.usernameDisplay\", \"creator.usernameDisplay\", \"owner.usernameDisplay.usernameDisplaySnowball\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"title.titleKStem^10.0F\", \"taxonomy.subject.label^1.1F\", \"taxonomy.course.label^1.4F\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\", \"resourceTitles\", \"collectionContents.description\", \"creator.usernameDisplay.usernameDisplayKStem\", \"owner.usernameDisplay.usernameDisplayKStem\", \"originalCreator.usernameDisplay.usernameDisplayKStem\" ], \"boost\" : 3.0, \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"gooru_kstem\" } } } }, \"from\" : 0, \"_source\" : [ \"id\", \"publishStatus\", \"learningObjective\", \"description\", \"contentFormat\", \"grade\", \"title\", \"collaboratorIds\", \"thumbnail\", \"createdAt\", \"updatedAt\", \"modifierId\", \"metadata\", \"statistics\", \"owner\", \"creator\", \"originalCreator\", \"taxonomy\", \"resourceTitles\", \"resourceIds\", \"collectionContents\", \"course\", \"license\" ] }";
  private static final String LANG_AGG_QUERY = "{\"query\": { \"bool\": { \"must\": [{ \"term\": { \"publishStatus\": \"published\" } }, { \"term\": { \"tenant.tenantId\": \"ba956a97-ae15-11e5-a302-f8a963065976\"} }], \"must_not\": []}}, \"aggs\" : { \"languages\" : { \"terms\" : { \"field\" : \"primaryLanguage.id\" } } } }";
  private static long OFFSET = 0;
  private static int BATCH_SIZE = 100;
  private static final int DAY_OF_MONTH = 1;
  private static final int HOUR_OF_DAY = 4;
  private static final int MINUTES = 21600;

  private static class PopulatGutSuggestionHolder {
    public static final PopulateSignatureCollectionJob INSTANCE = new PopulateSignatureCollectionJob();
  }

  public static PopulateSignatureCollectionJob instance() {
    return PopulatGutSuggestionHolder.INSTANCE;
  }
  
  @Override
  public void deployJob(JsonObject config) {
    LOGGER.info("Deploying Populate Signature Collection Job....");
    JsonObject params = config.getJsonObject("populateCollectionSuggestJobSettings");

    Integer dayOfMonth = params.getInteger("dayOfMonth", DAY_OF_MONTH);
    Integer hourOfDay = params.getInteger("hourOfDay", HOUR_OF_DAY);
    Integer minutes = params.getInteger("minutes", MINUTES);

    MinutesTimer.schedule(new Runnable() {
      public void run() {
        try {
            LazyList<IndexerJobStatus> jobDetails = IndexTrackerRepository.instance().getJobStatus("populate-signature-collection");
            String jobStatus = null;
            if (jobDetails != null && !jobDetails.isEmpty())
                jobStatus = ((IndexerJobStatus) (jobDetails.get(0))).getString("status");
            if (jobStatus != null && (jobStatus.equalsIgnoreCase("start") || jobStatus.equalsIgnoreCase("run-periodically"))) {
                                
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
          List<String> languages = getLanguageIdsToProcess();
          LOGGER.info("Languages to process : {}", languages);
          SignatureItemsRepository.instance().deleteSuggestions(IndexerConstants.COLLECTION);
          while (true) {
            JsonArray ltCodesArray = TaxonomyCodeRepository.instance().getLTCodeByFrameworkAndOffset(IndexerConstants.GUT_FRAMEWORK, limit, offset);
            if (ltCodesArray.size() == 0) {
              break;
            }
            languages.forEach(languageId -> {
              processTaxonomyCodes(ltCodesArray, languageId);
            });
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
            languages.forEach(languageId -> {
              processTaxonomyCodes(standardCodesArray, languageId);
            });
            totalProcessed += standardCodesArray.size();
            stdOffset += limit;
            if (totalProcessed >= totalCountOfStdsLts || totalProcessed >= expireCount) {
              break;
            }
          }
          if (!jobStatus.equalsIgnoreCase("run-periodically"))
              jobStatus = "completed";
          IndexTrackerRepository.instance().saveJobStatus("populate-signature-collection", jobStatus);
          LOGGER.info("Processed all standards count : {}", totalCountOfSTDs);
          LOGGER.info("Total processed std and lt codes : {} ", totalProcessed);
          LOGGER.info("Total time taken to populate : {}", (System.currentTimeMillis() - startTime));
          } else {
                LOGGER.info("populate-signature-collection is disabled");
            }
        } catch (Exception e) {
          LOGGER.info("Error while populating signature collections : Ex ::", e);
          e.printStackTrace();
        }
      }
    }, dayOfMonth, hourOfDay, minutes);
    //scheduler.cancelCurrent();
  }

  @SuppressWarnings("unchecked")
  private List<String> getLanguageIdsToProcess() {
    List<String> languages = null;
    try {
      Response culSearchResponse =
        performRequest("POST", "/" + IndexNameHolder.getIndexName(EsIndex.COLLECTION) + "/" + IndexerConstants.TYPE_COLLECTION + "/_search", LANG_AGG_QUERY);
      if (culSearchResponse.getEntity() != null) {
        languages = new ArrayList<>();
        Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(culSearchResponse.getEntity()),
          new TypeReference<Map<String, Object>>() {});
        Map<String, Object> aggsMap = (Map<String, Object>) responseAsMap.get("aggregations");
        List<Map<String, Object>> contentTypeAggList =
          (List<Map<String, Object>>) (((Map<String, Object>) aggsMap.get("languages")).get("buckets"));
        for (Map<String, Object> ctMap : contentTypeAggList) {
          languages.add(ctMap.get("key").toString());
        }
      }
    } catch (ParseException | IOException e) {
      LOGGER.info("PopulateSignatureCollectionJob : IO or Parse EXCEPTION: {} ", e);
    } catch (Exception e1) {
      LOGGER.info("PopulateSignatureCollectionJob : EXCEPTION: {} ", e1);
    }
    return languages;
  }
  
  @SuppressWarnings("unchecked")
  private void processTaxonomyCodes(JsonArray taxonomyCodeArray, String languageId) {
    for (Object taxonomyCode : taxonomyCodeArray) {
      JsonObject taxonomyCodeObject = (JsonObject) taxonomyCode;
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      try {
        if (!SignatureItemsRepository.instance().hasCuratedSuggestion(code, IndexerConstants.COLLECTION)) {
          LOGGER.debug("Proceed to populate, as suggestions are not present in table for code : {} ", code);

          if (!code.isEmpty()) {
            String query = QUERY.replaceAll("LANG_ID", languageId);
            query = query.replaceAll("GUT_CODE", convertArrayToString(StringUtils.join(code.toLowerCase(), IndexerConstants.COMMA)));
           Response searchResponse = performRequest("POST",
                    "/" + IndexNameHolder.getIndexName(EsIndex.COLLECTION) + "/" + IndexerConstants.TYPE_COLLECTION + "/_search", query);
            if (searchResponse.getEntity() != null) {
              Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                      new TypeReference<Map<String, Object>>() {});
              Map<String, Object> hitsMap = (Map<String, Object>) responseAsMap.get("hits");
              Long totalHits = ((Integer) hitsMap.get("total")).longValue();
              LOGGER.debug("search count : {}", totalHits);
              if (totalHits > 0) {
                Map<String, List<String>> suggestByPerfAsMap = new HashMap<>();
                deserializeResponseAndPopulate(taxonomyCodeObject, code, hitsMap, suggestByPerfAsMap);
                LOGGER.debug("Populated suggestions for code : {} : {} ", code, suggestByPerfAsMap.toString());
                if (!suggestByPerfAsMap.isEmpty()) {
                  extractAndPopulateSuggestions(taxonomyCodeObject, suggestByPerfAsMap, languageId);
                }
              } else {
                LOGGER.debug("No matching suggestions for gut : {} ", code);
              }
            }
          } else {
            LOGGER.debug("No mapping for gut : {} ", code);
          }
        } else {
          LOGGER.debug("Already suggestions are populated for this code : {} ", code);
        }
      } catch (Exception e) {
        LOGGER.debug("Error while checking or populating suggestions : {}, Exception : {}", code, e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void deserializeResponseAndPopulate(JsonObject taxonomyCodeObject, String code, Map<String, Object> hitsMap, Map<String, List<String>> suggestByPerfAsMap) {
    List<String> suggestIds = new ArrayList<>();
    List<String> highSuggestIds = new ArrayList<>();
    List<String> mediumSuggestIds = new ArrayList<>();
    List<String> suggestIdList = new ArrayList<>();
    long hitCount = ((Integer) hitsMap.get("total")).longValue();
    List<Map<String, Object>> hits = (List<Map<String, Object>>) (hitsMap).get("hits");
    for (Map<String, Object> hit : hits) {
      Map<String, Object> fields = (Map<String, Object>) hit.get("_source");
      String id = (String) fields.get(EntityAttributeConstants.ID);
      if (ContentRepository.instance().isOEExistInCollection(id)) continue;
      suggestIds.add(id);
      suggestIdList.add(id);
      if ((hitCount <= 3 && !(suggestIdList.size() < hitCount)) || suggestIdList.size() == 3) {
        highSuggestIds = suggestIds;
        suggestIds = new ArrayList<>();
      } else if ((!(suggestIdList.size() > 5) && !(suggestIdList.size() < hitCount)) || (hitCount >= 5 && suggestIdList.size() == 5)) {
        mediumSuggestIds = suggestIds;
        suggestIds = new ArrayList<>();
      }
      if (hitCount >= 5 && suggestIdList.size() == 5) {
        break;
      }
    }
    if (highSuggestIds.size() > 0)
      suggestByPerfAsMap.put(IndexerConstants.ABOVE_AVERAGE, highSuggestIds);
    if (mediumSuggestIds.size() > 0)
      suggestByPerfAsMap.put(IndexerConstants.AVERAGE, mediumSuggestIds);
    if (suggestIds.size() > 0)
      suggestByPerfAsMap.put(IndexerConstants.BELOW_AVERAGE, suggestIds);
    
  }

  private void extractAndPopulateSuggestions(JsonObject taxonomyCodeObject, Map<String, List<String>> perfMap, String languageId) {
    if (!perfMap.isEmpty()) {
      String code = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
      String codeType = taxonomyCodeObject.getString(EntityAttributeConstants.CODE_TYPE);
      String competency = null;
      String mcCompetency = null;
      if (IndexerConstants.STANDARD_MATCH.matcher(codeType).matches()) {
        competency = code;
      } else if (codeType.equalsIgnoreCase(IndexerConstants.LEARNING_TARGET_TYPE_0)) {
        mcCompetency = code;
        competency = taxonomyCodeObject.getString(TaxonomyCode.PARENT_TAXONOMY_CODE_ID);
      }
      for (Entry<String, List<String>> perfMapEntry : perfMap.entrySet()) {
        List<String> suggestIds = perfMapEntry.getValue();
        for (String itemId : suggestIds) {
          JsonObject suggestJson = new JsonObject();
          suggestJson.put(EntityAttributeConstants.COMPETENCY_GUT_CODE, competency);
          if (mcCompetency != null) {
            suggestJson.put(EntityAttributeConstants.MICRO_COMPETENCY_GUT_CODE, mcCompetency);
          }
          suggestJson.put(EntityAttributeConstants.PERFORMANCE_RANGE, perfMapEntry.getKey());
          suggestJson.put(EntityAttributeConstants.ITEM_ID, itemId);
          suggestJson.put(EntityAttributeConstants.ITEM_FORMAT, IndexerConstants.COLLECTION);
          suggestJson.put(EntityAttributeConstants.PRIMARY_LANGUAGE, Integer.valueOf(languageId));
          SignatureItemsRepository.instance().saveSuggestions(code, suggestJson);
        }
      }
    }
  }

}
