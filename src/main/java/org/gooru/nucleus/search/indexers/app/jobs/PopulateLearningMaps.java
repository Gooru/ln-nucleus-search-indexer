package org.gooru.nucleus.search.indexers.app.jobs;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants.LMContentFormat;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.LearningMapsRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyCodeRepository;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
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

public class PopulateLearningMaps  extends BaseIndexService implements JobInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateLearningMaps.class);
  private static final String GUT_QUERY = "{ \"post_filter\" : { \"bool\" : { \"filter\" : [ { \"term\" : { \"id\" : GUT_CODE } } ] } }, \"size\" : 10, \"query\" : { \"query_string\" : { \"query\" : \"*\", \"fields\" : [ \"title\", \"description\", \"codeType\", \"keywords\", \"competency.title\", \"competency.description\" ], \"tie_breaker\" : 1, \"default_operator\" : \"and\", \"allow_leading_wildcard\" : true } }, \"from\" : 0, \"_source\" : [ ] }";
  private static long OFFSET = 0;
  private static int BATCH_SIZE = 100;
  private static final int DAY_OF_MONTH = 1;
  private static final int HOUR_OF_DAY = 4;
  private static final int MINUTES = 21600;
  private static final String HOST = "http://staging.gooru.org";
  private static final String SEARCH_HOST = "http://localhost:8080";
  private static final Vertx vertx = Vertx.vertx();

  private static final WebClient client = WebClient.create(vertx);
  
  private static class PopulateLearningMapsTableHolder {
    public static final PopulateLearningMaps INSTANCE = new PopulateLearningMaps();
  }
  
  public static PopulateLearningMaps instance() {
    return PopulateLearningMapsTableHolder.INSTANCE;
  }
  
  @Override
  public void deployJob(JsonObject config) {
    LOGGER.info("Deploying Populate Learning Maps Job....");
    JsonObject params = config.getJsonObject("populateLearningMapsSettings");

    Integer dayOfMonth = params.getInteger("dayOfMonth", DAY_OF_MONTH);
    Integer hourOfDay = params.getInteger("hourOfDay", HOUR_OF_DAY);
    Integer minutes = params.getInteger("minutes", MINUTES);
    JsonObject configData = new JsonObject();
    configData.put("username", params.getString("username", "renuguru"));
    configData.put("password", params.getString("password", "test1"));
    configData.put("host", params.getString("host", HOST));
    configData.put("clientData", params.getJsonObject("clientData"));
    configData.put("searchHost", params.getString("searchHost", SEARCH_HOST));

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
          Future<String> tokenInFuture = generateToken(configData);
          processJob(tokenInFuture, configData, startTime, totalCountOfStdsLts, expireCount, limit, offset, totalProcessed);
        } catch (Exception e) {
          LOGGER.info("LM :: Error while populating table : Ex ::", e);
        }
      }

      private void processJob(Future<String> tokenInFuture, JsonObject configData, long startTime, Long totalCountOfStdsLts, Long expireCount, Integer limit, Long offset,
              Long totalProcessed) throws InterruptedException {
        if (tokenInFuture.isComplete() && tokenInFuture.succeeded() && tokenInFuture.result() != null) {
          String token = tokenInFuture.result();
          while (true) {
            JsonArray gutCodesArray =
                    TaxonomyCodeRepository.instance().getStdLTCodeByFrameworkAndOffset(IndexerConstants.GUT_FRAMEWORK, limit, offset);
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
          LOGGER.info("LM :: Total processed GUT codes for RQCACUL : {} ", totalProcessed);
          LOGGER.info("LM :: Total time taken to populate : {}", (System.currentTimeMillis() - startTime));
          return;
        } else {
          delay(500);
          LOGGER.info("LM :: Token generation in-progress, checking again : {}", tokenInFuture.result());
          processJob(tokenInFuture, configData, startTime, totalCountOfStdsLts, expireCount, limit, offset, totalProcessed);
        }
      }
    }, dayOfMonth, hourOfDay, minutes);
  }

  private void processGUTCodes(String token, JsonArray gutCodesArray, JsonObject configData) {
    LOGGER.info("token : " + token);
    String host = configData.getString("searchHost");
    for (Object gutCode : gutCodesArray) {
      try {
        JsonObject taxonomyCodeObject = (JsonObject) gutCode;
        String gut = taxonomyCodeObject.getString(EntityAttributeConstants.ID);
        LOGGER.info("ID : " + gut);
        Map<String, Object> lmJson = new HashMap<>();
        fetchSignatureContentStats(gut, lmJson);
        synchronized (this) {
          HttpClient httpClient = vertx.createHttpClient();
          HttpClientRequest httpClientRequest =
                  httpClient.getAbs(host + "/gooru-search/rest/v1/pedagogy-search/learning-maps/competency/" + gut + "?isAdmin=true", searchResponse -> {
                    if (searchResponse.statusCode() != 200) {
                      LOGGER.debug("fail" + searchResponse.statusMessage());
                    } else {
                      searchResponse.bodyHandler(responseBody -> {
                        JsonObject responseJson = new JsonObject(responseBody.toString());
                        JsonObject contents = responseJson.getJsonObject("contents");
                        for (LMContentFormat key : LMContentFormat.values()) {
                           generateContent(key.getValue(), lmJson, contents);
                        }
                        lmJson.put(EntityAttributeConstants.ID, gut);
                        LearningMapsRepository.instance().updateRQCACULLearningMaps(lmJson);
                      });
                    }
                  }).putHeader("Authorization", "Token " + token);
          httpClientRequest.end();
          delay(1000);
        }
      } catch (Exception e1) {
        LOGGER.info("PopulateLearningMapsTable : EXCEPTION: {} ", e1);
      }
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
        LOGGER.info("Received response with status code : " + response.statusCode() + " with accessToken : " + body.getString("access_token"));
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
      LOGGER.debug("InterruptedException : " , e);
    }
  }

  @SuppressWarnings("unchecked")
  private void fetchSignatureContentStats(String gut, Map<String, Object> lmJson) {
    try {
      String query = GUT_QUERY.replaceAll("GUT_CODE", convertArrayToString(StringUtils.join(gut, IndexerConstants.COMMA)));
      Response searchResponse = performRequest("POST", "/" + IndexNameHolder.getIndexName(EsIndex.GUT) + "/_search", query);
      if (searchResponse.getEntity() != null) {
        Map<String, Object> responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                new TypeReference<Map<String, Object>>() {
                });
        Map<String, Object> hitsMap = (Map<String, Object>) responseAsMap.get("hits");
        List<Map<String, Object>> hits = (List<Map<String, Object>>) hitsMap.get("hits");
        for (Map<String, Object> hit : hits) {
          Map<String, Object> source = (Map<String, Object>) hit.get("_source");
          List<Map<String, Object>> sc = (List<Map<String, Object>>) source.get("signatureCollections");
          List<Map<String, Object>> sr = (List<Map<String, Object>>) source.get("signatureResources");
          List<Map<String, Object>> sa = (List<Map<String, Object>>) source.get("signatureAssessments");

          lmJson.put("signature_collection_count", sc != null ? sc.size() : 0);
          lmJson.put("signature_resource_count", sr != null ? sr.size() : 0);
          lmJson.put("signature_assessment_count", sa != null ? sa.size() : 0);
        }   
      }
    } catch (Exception e) {
      LOGGER.info("PopulateLearningMapsTable : IO or Parse EXCEPTION: {} ", e);
    }
  }
  
  private static void generateContent(String contentType, Map<String, Object> lmJson, JsonObject contents) {
    JsonObject content = contents.getJsonObject(contentType);
    if (LMContentFormat.ASSESSMENT_EXTERNAL.getValue().equals(contentType)) {
      contentType = "ext_assessment";
    } else if (LMContentFormat.COLLECTION_EXTERNAL.getValue().equals(contentType)) {
      contentType = "ext_collection";
    } else if (LMContentFormat.OFFLINE_ACTIVITY.getValue().equals(contentType)) {
      contentType = "offline_activity";
    }
    lmJson.put(contentType +"_count", content.getInteger("totalHitCount"));
    lmJson.put(contentType, content.toString());
    if (contentType.equalsIgnoreCase(IndexerConstants.TYPE_RESOURCE)) LOGGER.info("Resource Count : {}",content.getInteger("totalHitCount"));
  }
  
}
