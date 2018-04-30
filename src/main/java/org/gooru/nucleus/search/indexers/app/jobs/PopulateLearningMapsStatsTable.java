package org.gooru.nucleus.search.indexers.app.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LearningMapsRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class PopulateLearningMapsStatsTable extends BaseIndexService implements JobInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateLearningMapsStatsTable.class);
  private static final String AGG_QUERY = "{ \"size\" :0, \"query\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"publishStatus\" : \"published\" } }, { \"term\" : { \"tenant.tenantId\" : \"ba956a97-ae15-11e5-a302-f8a963065976\" } },{\"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"all\", \"description^1.5F\", \"text\", \"tags^3.0F\", \"title^5.0F\", \"narration\", \"collectionTitles\", \"originalCreator.usernameDisplay\", \"creator.usernameDisplay\", \"originalCreator.usernameDisplay.usernameDisplaySnowball\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"taxonomy.course.label^1.4F\", \"taxonomy.subject.label^1.1F\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\", \"resourceSource.attribution\", \"copyrightOwnerList.copyrightOwnerListSnowball\", \"info.publisher\", \"info.publisher.publisherSnowball\", \"copyrightOwnerList.copyrightOwnerListStandard\" ], \"boost\" : 1.0, \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"standard\" } } ] } }, \"_source\" : [ ], \"aggs\" : { \"gut\" : { \"terms\" : { \"field\" : \"taxonomy.gutCodes.keyword\" , \"size\" : 4000}, \"aggs\": { \"contentType\": { \"terms\": { \"field\": \"contentFormat.keyword\" } } } } } }";
  private static final String TAX_QUERY = "{ \"post_filter\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"gutCodes\" : GUT_CODE } } ] } }, \"size\" : 10, \"query\" : { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"title\", \"description\", \"codeType\", \"keywords\", \"competency.title\", \"competency.description\" ], \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : true } }, \"from\" : 0, \"_source\" : [ \"id\", \"codeType\", \"title\", \"description\", \"gutCodes\", \"keywords\", \"course\", \"subject\", \"domain\", \"competency\", \"gutPrerequisites\", \"gutData\", \"code\" ] }";
  private static final String CUL_QUERY = "{ \"query\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"tenant.tenantId\" : \"ba956a97-ae15-11e5-a302-f8a963065976\" } }, { \"nested\" : { \"path\" : \"taxonomy.subject\", \"query\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"taxonomy.subject.codeId\" : GUT_SUBJECT } } ] } } } }, { \"term\" : { \"publishStatus\" : \"published\" } }, { \"query_string\" : { \"query\" : KEYWORD_QUERY, \"fields\" : [ \"_all\", \"all\", \"title^5.0F\", \"collectionTitles\", \"creator.usernameDisplay\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"taxonomy.course.label\", \"taxonomy.subject.label\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\" ], \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"standard\" } } ] } }, \"size\" : 10, \"from\" : 0, \"aggs\": { \"contentType\": { \"terms\": { \"field\": \"contentFormat.keyword\" } } } }";
  private static final String CUL_GUT_QUERY = "{ \"query\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"taxonomy.relatedGutCodes\" : GUT_CODE } } ,{ \"term\" : { \"tenant.tenantId\" : \"ba956a97-ae15-11e5-a302-f8a963065976\" } }, { \"term\" : { \"publishStatus\" : \"published\" } }, { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"_all\", \"all\", \"title^5.0F\", \"collectionTitles\", \"creator.usernameDisplay\", \"creator.usernameDisplay.usernameDisplaySnowball\", \"taxonomy.course.label\", \"taxonomy.subject.label\", \"taxonomy.domain.label\", \"taxonomy.domain.label.labelSnowball\", \"taxonomy.course.label.labelSnowball\", \"taxonomy.subject.label.labelSnowball\" ], \"use_dis_max\" : true, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : false, \"analyzer\" : \"standard\" } } ] } }, \"size\" : 10, \"from\" : 0, \"aggs\": { \"contentType\": { \"terms\": { \"field\": \"contentFormat.keyword\" } } } }";
  private static long OFFSET = 0;
  private static int BATCH_SIZE = 100;
  private static final int DAY_OF_MONTH = 1;
  private static final int HOUR_OF_DAY = 4;
  private static final int MINUTES = 21600;
  
  private static class PopulateLearningMapsTableHolder {
    public static final PopulateLearningMapsStatsTable INSTANCE = new PopulateLearningMapsStatsTable();
  }

  public static PopulateLearningMapsStatsTable instance() {
    return PopulateLearningMapsTableHolder.INSTANCE;
  }
  
  @Override
  public void deployJob(JsonObject config) {
    LOGGER.info("Deploying Populate Learning Maps Job....");
    JsonObject params = config.getJsonObject("populateLearningMapsStatsTableSettings");

    Integer dayOfMonth = params.getInteger("dayOfMonth", DAY_OF_MONTH);
    Integer hourOfDay = params.getInteger("hourOfDay", HOUR_OF_DAY);
    Integer minutes = params.getInteger("minutes", MINUTES);

    MonthlyTimer.schedule(new Runnable() {
      public void run() {
        try {
          long startTime = System.currentTimeMillis();
          LOGGER.info("Starting Populate Learning Maps Job....");
          Long totalCountOfStdsLts = TaxonomyCodeRepository.instance().getStandardLtsCountByFramework(IndexerConstants.GUT_FRAMEWORK);
          LOGGER.info("Total no:of codes to process : {}", totalCountOfStdsLts);
          Long expireCount = params.getLong("processCount", totalCountOfStdsLts);
          Integer limit = params.getInteger("batchSize", BATCH_SIZE);
          Long offset = params.getLong("offset", OFFSET);
          Long totalProcessed = 0L;

          processContents();

          while (true) {
            JsonArray gutCodesArray =
                    TaxonomyCodeRepository.instance().getStdLTCodeByFrameworkAndOffset(IndexerConstants.GUT_FRAMEWORK, limit, offset);
            if (gutCodesArray.size() == 0) {
              break;
            }
            processGUTCodes(gutCodesArray);
            totalProcessed += gutCodesArray.size();
            offset += limit;
            if (totalProcessed >= totalCountOfStdsLts || totalProcessed >= expireCount) {
              break;
            }
          }

          LOGGER.info("LM :: Total processed GUT codes for CUL : {} ", totalProcessed);
          LOGGER.info("LM :: Total time taken to populate : {}", (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
          LOGGER.info("LM :: Error while populating table : Ex ::", e);
        }
      }
    }, dayOfMonth, hourOfDay, minutes);
    // scheduler.cancelCurrent();
  }

  @SuppressWarnings("unchecked")
  private void processGUTCodes(JsonArray gutCodesArray) {

    gutCodesArray.forEach(gutCode -> {
      try {
        JsonObject taxonomyCodeObject = (JsonObject) gutCode;
        String gut = taxonomyCodeObject.getString(EntityAttributeConstants.ID).toLowerCase();

        Map<String, Object> lmJson = new HashMap<>();
        String query = TAX_QUERY.replaceAll("GUT_CODE", convertArrayToString(StringUtils.join(gut, IndexerConstants.COMMA)));
        Response searchResponse = performRequest("POST", "/" + IndexNameHolder.getIndexName(EsIndex.TAXONOMY) + "/_search", query);
        if (searchResponse.getEntity() != null) {
          Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                  new TypeReference<Map<String, Object>>() {
                  });
          Map<String, Object> hitsMap = (Map<String, Object>) responseAsMap.get("hits");
          List<Map<String, Object>> hits = (List<Map<String, Object>>) hitsMap.get("hits");
          for (Map<String, Object> hit : hits) {
            Map<String, Object> source = (Map<String, Object>) hit.get("_source");
            List<Map<String, Object>> gutAsList = (List<Map<String, Object>>) source.get("gutData");
            for (Map<String, Object> gutAsMap : gutAsList) {
              Map<String, Object> gutData = (Map<String, Object>) gutAsMap;
              if (gutData.get("id").toString().equalsIgnoreCase(gut)) {
                List<String> sc = (List<String>) gutData.get("signatureCollections");
                List<String> sr = (List<String>) gutData.get("signatureResources");
                List<String> sa = (List<String>) gutData.get("signatureAssessments");

                lmJson.put("signature_collection", sc != null ? sc.size() : 0);
                lmJson.put("signature_resource", sr != null ? sr.size() : 0);
                lmJson.put("signature_assessment", sa != null ? sa.size() : 0);
              } else {
                continue;
              }
            }
          }
        }

//        String gutSubject = gut.substring(0, gut.indexOf("-"));
//        String culQuery = CUL_QUERY.replaceAll("GUT_SUBJECT", convertArrayToString(StringUtils.join(gutSubject, IndexerConstants.COMMA)));
//        culQuery = culQuery.replaceAll("KEYWORD_QUERY", convertArrayToString(StringUtils.join(queryString, IndexerConstants.COMMA)));
        String culQuery = CUL_GUT_QUERY.replaceAll("GUT_CODE", convertArrayToString(StringUtils.join(gut, IndexerConstants.COMMA)));

        Response culSearchResponse = performRequest("POST", "/" + IndexNameHolder.getIndexName(EsIndex.COURSE) + ","
                + IndexNameHolder.getIndexName(EsIndex.UNIT) + "," + IndexNameHolder.getIndexName(EsIndex.LESSON) + "/_search", culQuery);
        if (searchResponse.getEntity() != null) {
          Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(culSearchResponse.getEntity()),
                  new TypeReference<Map<String, Object>>() {
                  });
          Map<String, Object> aggsMap = (Map<String, Object>) responseAsMap.get("aggregations");
          List<Map<String, Object>> contentTypeAggList =
                  (List<Map<String, Object>>) (((Map<String, Object>) aggsMap.get("contentType")).get("buckets"));
          lmJson.put("id", gut.toUpperCase());
          for (Map<String, Object> ctMap : contentTypeAggList) {
            lmJson.put(ctMap.get("key").toString(), (Integer) ctMap.get("doc_count"));
          }
          LearningMapsRepository.instance().updateCULForLearningMaps(lmJson);
        }

      } catch (ParseException | IOException e) {
        LOGGER.info("PopulateLearningMapsTable : IO or Parse EXCEPTION: {} ", e);
      } catch (Exception e1) {
        LOGGER.info("PopulateLearningMapsTable : EXCEPTION: {} ", e1);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void processContents() {
    try {
      Response searchResponse = performRequest("POST", "/" + IndexNameHolder.getIndexName(EsIndex.RESOURCE) + ","
              + IndexNameHolder.getIndexName(EsIndex.COLLECTION) + "," + IndexNameHolder.getIndexName(EsIndex.RUBRIC) + "/_search", AGG_QUERY);
      if (searchResponse.getEntity() != null) {
        Map<String, Object> responseAsMap;
        responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                new TypeReference<Map<String, Object>>() {
                });
        List<Map<String, Object>> lmArray = new ArrayList<>();
        Map<String, Object> aggsMap = (Map<String, Object>) responseAsMap.get("aggregations");
        Map<String, Object> gutsMap = (Map<String, Object>) aggsMap.get("gut");
        List<Map<String, Object>> gutsBucket = (List<Map<String, Object>>) gutsMap.get("buckets");
        gutsBucket.forEach(map -> {
          String id = map.get("key").toString();
          List<Map<String, Object>> contentTypeAggList = (List<Map<String, Object>>) (((Map<String, Object>) map.get("contentType")).get("buckets"));
          Map<String, Object> lmJson = new HashMap<>();
          lmJson.put("id", id.toUpperCase());
          for (Map<String, Object> ctMap : contentTypeAggList) {
            lmJson.put(ctMap.get("key").toString(), (Integer) ctMap.get("doc_count"));
          }
          lmArray.add(lmJson);
        });
        LOGGER.info("Processed all learning maps count : {}", lmArray.size());
        LearningMapsRepository.instance().updateRQCALearningMaps(lmArray);
      }
    } catch (ParseException | IOException e) {
      LOGGER.info("PopulateLearningMapsTable : IO or Parse EXCEPTION: {} ", e);
    } catch (Exception e1) {
      LOGGER.info("PopulateLearningMapsTable : EXCEPTION: {} ", e1);
    }
  }

}
