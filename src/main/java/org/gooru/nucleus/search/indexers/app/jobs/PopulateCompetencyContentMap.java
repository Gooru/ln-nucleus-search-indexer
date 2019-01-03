package org.gooru.nucleus.search.indexers.app.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CompetencyContentMapRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexTrackerRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.IndexerJobStatus;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class PopulateCompetencyContentMap extends BaseIndexService implements JobInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PopulateCompetencyContentMap.class);
    private static long OFFSET = 0;
    private static int BATCH_SIZE = 100;
    private static final int DAY_OF_MONTH = 1;
    private static final int HOUR_OF_DAY = 4;
    private static final int MINUTES = 0;
    private static final String HOST = "http://staging.gooru.org";
    private static final Vertx vertx = Vertx.vertx();
    private static final String LANG_AGG_QUERY = "{\"query\": { \"bool\": { \"must\": [{ \"term\": { \"publishStatus\": \"published\" } }, { \"term\": { \"tenant.tenantId\": \"ba956a97-ae15-11e5-a302-f8a963065976\"} }], \"must_not\": []}}, \"aggs\" : { \"languages\" : { \"terms\" : { \"field\" : \"primaryLanguage.id\" } } } }";

    private static final WebClient client = WebClient.create(vertx);

    private static class PopulateLearningMapsTableHolder {
        public static final PopulateCompetencyContentMap INSTANCE = new PopulateCompetencyContentMap();
    }

    public static PopulateCompetencyContentMap instance() {
        return PopulateLearningMapsTableHolder.INSTANCE;
    }

    @Override
    public void deployJob(JsonObject config) {
        LOGGER.info("Deploying Populate Competency Content Map Job....");
        JsonObject params = config.getJsonObject("populateCompetencyContentMapSettings");

        Integer dayOfMonth = params.getInteger("dayOfMonth", DAY_OF_MONTH);
        Integer hourOfDay = params.getInteger("hourOfDay", HOUR_OF_DAY);
        Integer minutes = params.getInteger("minutes", MINUTES);
        JsonObject configData = new JsonObject();
        configData.put("username", params.getString("username", "renuguru"));
        configData.put("password", params.getString("password", "test1"));
        configData.put("host", params.getString("host", HOST));
        configData.put("clientData", params.getJsonObject("clientData"));

        MonthlyTimer.schedule(new Runnable() {
            public void run() {
                try {
                    LazyList<IndexerJobStatus> jobDetails =
                        IndexTrackerRepository.instance().getJobStatus("populate-competency-content-map");
                    String jobStatus = null;
                    if (jobDetails != null && !jobDetails.isEmpty())
                        jobStatus = ((IndexerJobStatus) (jobDetails.get(0))).getString("status");
                    if (jobStatus != null
                        && (jobStatus.equalsIgnoreCase("start") || jobStatus.equalsIgnoreCase("run-periodically"))) {

                        long startTime = System.currentTimeMillis();
                        LOGGER.info("Starting Populate Competency Content Map Job....");
                        Long totalCountOfStdsLts = TaxonomyCodeRepository.instance()
                            .getStandardLtsCountByFramework(IndexerConstants.GUT_FRAMEWORK);
                        LOGGER.info("Total no:of codes to process : {}", totalCountOfStdsLts);
                        Long expireCount = params.getLong("processCount", totalCountOfStdsLts);
                        Integer limit = params.getInteger("batchSize", BATCH_SIZE);
                        Long offset = params.getLong("offset", OFFSET);
                        Long totalProcessed = 0L;
                        Future<String> tokenInFuture = generateToken(configData);
                        processJob(tokenInFuture, configData, startTime, totalCountOfStdsLts, expireCount, limit,
                            offset, totalProcessed);
                        if (!jobStatus.equalsIgnoreCase("run-periodically"))
                            jobStatus = "completed";
                        IndexTrackerRepository.instance().saveJobStatus("mc-r-standard", jobStatus);
                    } else {
                        LOGGER.info("populate-competency-content-map is disabled");
                    }
                } catch (Exception e) {
                    LOGGER.info("CCM :: Error while populating table : Ex ::", e);
                }
            }

            private void processJob(Future<String> tokenInFuture, JsonObject configData, long startTime,
                Long totalCountOfStdsLts, Long expireCount, Integer limit, Long offset, Long totalProcessed)
                throws InterruptedException {
                if (tokenInFuture.isComplete() && tokenInFuture.succeeded() && tokenInFuture.result() != null) {
                    String token = tokenInFuture.result();
                    while (true) {
                        JsonArray gutCodesArray = TaxonomyCodeRepository.instance()
                            .getStdLTCodeByFrameworkAndOffset(IndexerConstants.GUT_FRAMEWORK, limit, offset);
                        if (gutCodesArray.size() == 0) {
                            break;
                        }
                        processGUTCodes(token, gutCodesArray, configData);
                        totalProcessed += gutCodesArray.size();
                        offset += limit;
                        LOGGER.info("Processed count : {} ", totalProcessed);

                        if (totalProcessed >= totalCountOfStdsLts || totalProcessed >= expireCount) {
                            break;
                        }
                    }
                    LOGGER.info("CCM :: Total processed GUT codes for CCM : {} ", totalProcessed);
                    LOGGER.info("CCM :: Total time taken to populate : {}", (System.currentTimeMillis() - startTime));
                    return;
                } else {
                    delay(500);
                    LOGGER.info("CCM :: Token generation in-progress, checking again : {}", tokenInFuture.result());
                    processJob(tokenInFuture, configData, startTime, totalCountOfStdsLts, expireCount, limit, offset,
                        totalProcessed);
                }
            }
        }, dayOfMonth, hourOfDay, minutes);
    }

    @SuppressWarnings("unchecked")
    private List<String> getLanguageIdsToProcess() {
      List<String> languageIds = null;
      try {
        Response culSearchResponse =
          performRequest("POST", "/" + IndexNameHolder.getIndexName(EsIndex.COLLECTION) + "/" + IndexerConstants.TYPE_COLLECTION + "/_search", LANG_AGG_QUERY);
        if (culSearchResponse.getEntity() != null) {
          languageIds = new ArrayList<>();
          Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(culSearchResponse.getEntity()),
            new TypeReference<Map<String, Object>>() {});
          Map<String, Object> aggsMap = (Map<String, Object>) responseAsMap.get("aggregations");
          List<Map<String, Object>> contentTypeAggList =
            (List<Map<String, Object>>) (((Map<String, Object>) aggsMap.get("languages")).get("buckets"));
          for (Map<String, Object> ctMap : contentTypeAggList) {
            languageIds.add(ctMap.get("key").toString());
          }
        }
      } catch (ParseException | IOException e) {
        LOGGER.info("PopulateCompetencyContentMap : IO or Parse EXCEPTION: {} ", e);
      } catch (Exception e1) {
        LOGGER.info("PopulateCompetencyContentMap : EXCEPTION: {} ", e1);
      }
      return languageIds;
    }
    
    private void processGUTCodes(String token, JsonArray gutCodesArray, JsonObject configData) {
        LOGGER.info("token : " + token);
        String host = configData.getString("host");
        List<String> languageIds = getLanguageIdsToProcess();
        for (Object gutCode : gutCodesArray) {
          languageIds.forEach(langId -> {
            try {
                JsonObject taxonomyCodeObject = (JsonObject) gutCode;
                String gut = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
                String codeType = taxonomyCodeObject.getString(EntityAttributeConstants.CODE_TYPE);
                LOGGER.info("ID : " + gut);
                synchronized (this) {
                    HttpClient httpClient = vertx.createHttpClient();
                    HttpClientRequest httpClientRequest =
                        httpClient.getAbs(host + "/gooru-search/rest/v1/pedagogy-search/learning-maps/competency/" + gut
                            + "?isAdmin=true&length=15&flt.languageId="+langId, searchResponse -> {
                                if (searchResponse.statusCode() != 200) {
                                    LOGGER.debug("fail" + searchResponse.statusMessage());
                                } else {
                                    searchResponse.bodyHandler(responseBody -> {
                                        JsonObject responseJson = new JsonObject(responseBody.toString());
                                        JsonObject contents = responseJson.getJsonObject("contents");
                                        List<Map<String, Object>> competencyCollectionList = new ArrayList<>();
                                        generateContent(IndexerConstants.TYPE_COLLECTION, competencyCollectionList,
                                            contents, gut, codeType, langId);
                                        CompetencyContentMapRepository.instance()
                                            .insertToCompetencyContentMap(competencyCollectionList);

                                        List<Map<String, Object>> competencyAssessmentList = new ArrayList<>();
                                        generateContent(IndexerConstants.TYPE_ASSESSMENT, competencyAssessmentList,
                                            contents, gut, codeType, langId);
                                        CompetencyContentMapRepository.instance()
                                            .insertToCompetencyContentMap(competencyAssessmentList);
                                    });
                                }
                            }).putHeader("Authorization", "Token " + token);
                    httpClientRequest.end();
                    delay(2000);
                }
            } catch (Exception e1) {
                LOGGER.info("PopulateCompetencyContentMap : EXCEPTION: {} ", e1);
            }
        });
        }
    }

    private static Future<String> generateToken(JsonObject config) {
        Future<String> future = Future.future();
        String userId = config.getString("username");
        String password = config.getString("password");
        String host = config.getString("host");
        String encodedAuth = getAuthorizedWithCredential(userId, password);
        JsonObject requestBody = config.getJsonObject("clientData");
        HttpRequest<Buffer> httpRequest = client.postAbs(host + "/api/nucleus-auth/v2/signin");
        MultiMap headers = httpRequest.headers();
        headers.set("content-type", "application/json");
        headers.set("Authorization", "Basic " + encodedAuth);
        httpRequest.as(BodyCodec.jsonObject()).sendJsonObject(requestBody, ar -> {
            if (ar.succeeded()) {
                HttpResponse<JsonObject> response = ar.result();
                JsonObject body = response.body();
                LOGGER.info("Received response with status code : " + response.statusCode() + " with accessToken : "
                    + body.getString("access_token"));
                future.complete(ar.result().body().getString("access_token"));
            } else {
                LOGGER.info("Something went wrong " + ar.cause());
                future.fail(ar.cause().getMessage());
            }
        });
        return future;
    }

    private static String getAuthorizedWithCredential(String userId, String password) {
        String combinedString = userId + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(combinedString.getBytes());
        LOGGER.info("Authorization: Basic " + encodedAuth);
        return encodedAuth;
    }

    private void delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            LOGGER.debug("InterruptedException : ", e);
        }
    }

    private static void generateContent(String contentType, List<Map<String, Object>> competencyContentList,
        JsonObject contents, String gut, String codeType, String languageId) {
        JsonObject content = contents.getJsonObject(contentType);
        JsonArray searchResults = content.getJsonArray("searchResults");
        for (Object sr : searchResults) {
            Map<String, Object> dataMap = new HashMap<>();
            JsonObject r = (JsonObject) sr;
            String id = r.getString("id");
            if (CompetencyContentMapRepository.instance().contentAlreadyExistsInCompetencyContentMap(id, contentType))
                continue;
            if (IndexerConstants.STANDARD_MATCH.matcher(codeType).matches())
                dataMap.put("competency", gut);
            else if (codeType.equalsIgnoreCase(IndexerConstants.LEARNING_TARGET_TYPE_0)) {
                dataMap.put("micro_competency", gut);
                dataMap.put("competency", gut.substring(0, StringUtils.ordinalIndexOf(gut, "-", 4)));
            }
            dataMap.put("domain", gut.substring(0, StringUtils.ordinalIndexOf(gut, "-", 3)));
            dataMap.put("course", gut.substring(0, StringUtils.ordinalIndexOf(gut, "-", 2)));
            dataMap.put("subject", gut.substring(0, StringUtils.ordinalIndexOf(gut, "-", 1)));
            dataMap.put("content_type", contentType);
            dataMap.put("item_id", id);
            Integer questionCount = r.getInteger("questionCount");
            Integer resourceCount = r.getInteger("resourceCount");
            dataMap.put("item_count", questionCount + resourceCount);
            dataMap.put("is_published", r.getString("publishStatus").equalsIgnoreCase("published") ? true : false);
            dataMap.put("is_featured", r.getBoolean("isFeatured"));
            dataMap.put(EntityAttributeConstants.PRIMARY_LANGUAGE, Integer.valueOf(languageId));
            competencyContentList.add(dataMap);
            if (competencyContentList.size() == 5)
                break;
        }
    }

}
