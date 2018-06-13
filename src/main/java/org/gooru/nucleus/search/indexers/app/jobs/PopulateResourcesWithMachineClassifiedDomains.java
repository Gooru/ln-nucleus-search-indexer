package org.gooru.nucleus.search.indexers.app.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexTrackerRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.MachineClassifyContentRepository;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.TaxonomyRepository;
import org.gooru.nucleus.search.indexers.app.repositories.entities.IndexerJobStatus;
import org.gooru.nucleus.search.indexers.app.services.BaseIndexService;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.vertx.core.json.JsonObject;

public class PopulateResourcesWithMachineClassifiedDomains extends BaseIndexService implements JobInitializer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PopulateResourcesWithMachineClassifiedDomains.class);

  private static final String GET_NO_STD_CONTENT_QUERY = "{ \"size\": BATCH_SIZE, \"query\" : { \"bool\" : { \"filter\" : [{ \"term\" : { \"contentFormat\" : \"resource\" } }, { \"term\" : { \"tenant.tenantId\" : \"ba956a97-ae15-11e5-a302-f8a963065976\" } }, { \"term\" : { \"publishStatus\" : \"published\" } },{\"exists\":{\"field\":\"taxonomy.taxonomySet.course\"}},{\"exists\":{\"field\":\"taxonomy.taxonomySet.domain\"}}, { \"term\" : { \"taxonomy.hasStandard\" : \"0\" } } ], \"must_not\":[] } }, \"_source\":[\"title\",\"description\",\"taxonomy\",\"id\"]}";
  private static final String GET_SIMILAR_CONTENT_WITH_STD_QUERY = "{ \"query\": { \"bool\": { \"must\": [ { \"more_like_this\" : { \"fields\" : [\"title\", \"description\",\"resourceInfo.text\",\"taxonomy.subject.label\", \"taxonomy.course.label\", \"taxonomy.domain.label\"], \"like\" : [ { \"_index\" : \"gooru_nile_resource_v2\", \"_type\" : \"resource\", \"_id\" : \"RESOURCE_ID\" } ], \"min_term_freq\" : 1, \"max_query_terms\" : 5000 } } ], \"filter\": { \"bool\":{\"must\":[{\"term\": { \"taxonomy.hasStandard\": 1 } }, { \"nested\" : { \"path\" : \"taxonomy.subject\", \"query\" : { \"bool\" : { \"filter\" : [ { \"terms\" : { \"taxonomy.subject.codeId\" : [SUBJECT_IDS] } } ] } } } }, { \"nested\" : { \"path\" : \"taxonomy.course\", \"query\" : { \"bool\" : { \"filter\" : [ { \"terms\" : { \"taxonomy.course.codeId\" : [COURSE_IDS] } } ] } } } }, { \"nested\" : { \"path\" : \"taxonomy.domain\", \"query\" : { \"bool\" : { \"filter\" : [ { \"terms\" : { \"taxonomy.domain.codeId\" : [DOMAIN_IDS] } } ] } } } }]} } }}, \"_source\":[\"title\",\"description\", \"taxonomy\"], \"aggs\":{ \"gut_group\":{ \"terms\":{ \"field\": \"taxonomy.gutCodes.keyword\", \"size\": 30 } } } }";
  private static long OFFSET = 0;
  private static int BATCH_SIZE = 100;
  private static final int DAY_OF_MONTH = 1;
  private static final int HOUR_OF_DAY = 4;
  private static final int MINUTES = 21600;

  private static class PopulatedResourcesWithMachineClassifiedTagsHolder {
    public static final PopulateResourcesWithMachineClassifiedDomains INSTANCE = new PopulateResourcesWithMachineClassifiedDomains();
  }

  public static PopulateResourcesWithMachineClassifiedDomains instance() {
    return PopulatedResourcesWithMachineClassifiedTagsHolder.INSTANCE;
  }
  
  @Override
  public void deployJob(JsonObject config) {
    LOGGER.info("Deploying Machine Classify Resource Job....");
    JsonObject params = config.getJsonObject("machineClassifyResourceToDoaminJobSettings");

    Integer dayOfMonth = params.getInteger("dayOfMonth", DAY_OF_MONTH);
    Integer hourOfDay = params.getInteger("hourOfDay", HOUR_OF_DAY);
    Integer minutes = params.getInteger("minutes", MINUTES);

    MinutesTimer.schedule(new Runnable() {
      public void run() {
        Integer limit = params.getInteger("batchSize", 10);
        try {
            LazyList<IndexerJobStatus> jobDetails = IndexTrackerRepository.instance().getJobStatus("mc-r-domain");
            String jobStatus = null;
            if (jobDetails != null && !jobDetails.isEmpty())
                jobStatus = ((IndexerJobStatus) (jobDetails.get(0))).getString("status");
            if (jobStatus != null && jobStatus.equalsIgnoreCase("start") || jobStatus.equalsIgnoreCase("run-periodically")) {
                long startTime = System.currentTimeMillis();
                LOGGER.info("Starting job for Machine classify resource to domain....");

                Long classifyLimit = params.getLong("classifyLimit", null);

                classifyResources(classifyLimit, limit, jobStatus);

                LOGGER.info("Total time taken to classify : {}", (System.currentTimeMillis() - startTime));
            } else {
                LOGGER.info("MC-R-DOMAIN Job is disabled");
            }
        } catch (Exception e) {
          LOGGER.info("Error while populating Machine Classify Resources : Ex ::", e);
          e.printStackTrace();
        }
      }
    }, dayOfMonth, hourOfDay, minutes);
  }

  @SuppressWarnings("unchecked")
  private void classifyResources(Long classifyLimit, Integer batchSize, String jobStatus) {
      try {
          int classifiedTotal = 0;
          int processedCount = 0;

          String trainingDataQuery = GET_NO_STD_CONTENT_QUERY.replaceAll("BATCH_SIZE", batchSize + "");
          Response searchResponse = performRequest("POST", "/" + IndexNameHolder.getIndexName(EsIndex.RESOURCE) + "/"
              + IndexerConstants.TYPE_RESOURCE + "/_search?scroll=1m", trainingDataQuery);
          if (searchResponse.getEntity() != null) {
              Map<String, Object> responseAsMap =
                  (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(searchResponse.getEntity()),
                      new TypeReference<Map<String, Object>>() {});
              Map<String, Object> hitsMap = (Map<String, Object>) responseAsMap.get("hits");
              Long totalHits = ((Integer) hitsMap.get("total")).longValue();
              if (totalHits > 0) {
                  while (true) {
                      LOGGER.info("Processing next batch starting from : {} to : {}", processedCount + 1,
                          processedCount + batchSize);
                      List<Map<String, Object>> hits = (List<Map<String, Object>>) (hitsMap).get("hits");
                      for (Map<String, Object> hit : hits) {
                          processedCount++;
                          Map<String, Object> fields = (Map<String, Object>) hit.get("_source");
                          String id = (String) hit.get("_id");
                          if (MachineClassifyContentRepository.instance().hasDomainClassification(id)) {
                              LOGGER.info("ID {} Already processed!", id);
                              continue;
                          }
                          Map<String, Object> taxonomy =
                              (Map<String, Object>) fields.get(EntityAttributeConstants.TAXONOMY);
                          List<Map<String, String>> subjectList =
                              (List<Map<String, String>>) taxonomy.get(IndexFields.SUBJECT);
                          Set<String> subjectArray = new HashSet<>();
                          if (subjectList.size() > 0) {
                              for (Map<String, String> subjectMap : subjectList) {
                                  subjectArray.add(subjectMap.get(IndexFields.CODE_ID));
                              }
                          }
                          Set<String> courseArray = new HashSet<>();
                          List<Map<String, String>> courseList =
                              (List<Map<String, String>>) taxonomy.get(IndexFields.COURSE);
                          List<String> allPossibleDomainOfResCourse = new ArrayList<>();

                          if (courseList.size() > 0) {
                              for (Map<String, String> courseMap : courseList) {
                                  courseArray.add(courseMap.get(IndexFields.CODE_ID));
                                  List<String> domains = TaxonomyRepository.instance().getAllDomainUnderCourseByFw(
                                      courseMap.get(IndexFields.CODE_ID), IndexerConstants.GUT_FRAMEWORK);
                                  if (domains != null && domains.size() > 0)
                                      allPossibleDomainOfResCourse.addAll(domains);
                              }
                          }

                          LOGGER.info("Resource ID : {} subjectArray : {} courseArray : {}", id, subjectArray,
                              courseArray);
                          if (subjectArray.size() > 0 && courseArray.size() > 0) {
                              Set<String> finalDomains =
                                  classifyResource(id, subjectArray, courseArray, allPossibleDomainOfResCourse);
                              LOGGER.info("Mapped Domain : {}", finalDomains);

                              storeClassification(id, finalDomains);

                              classifiedTotal++;
                          }
                          if (classifyLimit != null && classifiedTotal == classifyLimit) {
                              break;
                          }
                      }

                      String scrollId = (String) responseAsMap.get("_scroll_id");
                      JsonObject scroll = new JsonObject();
                      scroll.put("scroll", "1m");
                      scroll.put("scroll_id", scrollId);
                      searchResponse = performRequest("POST", "/_search/scroll", scroll.toString());
                      if (searchResponse.getEntity() != null) {
                          responseAsMap = (Map<String, Object>) SERIAILIZER.readValue(
                              EntityUtils.toString(searchResponse.getEntity()),
                              new TypeReference<Map<String, Object>>() {
                              });
                          hitsMap = (Map<String, Object>) responseAsMap.get("hits");
                          hits = (List<Map<String, Object>>) hitsMap.get("hits");
                          if ((hitsMap != null && hits.size() == 0) || (classifyLimit != null && classifiedTotal == classifyLimit)) {
                              LOGGER.warn("Reached Export Limit!");
                              LOGGER.info("Total contents machine classified : {}", classifiedTotal);
                              break;
                          }
                      }
                  }
              }
          }
          if (!jobStatus.equalsIgnoreCase("run-periodically"))
              jobStatus = "completed";
          IndexTrackerRepository.instance().saveJobStatus("mc-r-domain", jobStatus);
      } catch (Exception e) {
          LOGGER.info("Error while classifying : Exception : {}", e);
          e.printStackTrace();
      }
  }

  private void storeClassification(String id, Set<String> finalDomains) {
      Map<String, Object> data = new HashMap<>();
      data.put("id", id);
      data.put("machine_classified_tags", finalDomains);
      MachineClassifyContentRepository.instance().saveMachineClassifiedDomains(id, data);
  }

  @SuppressWarnings("unchecked")
  private Set<String> classifyResource(String id, Set<String> subjectArray, Set<String> courseArray,
      List<String> allPossibleDomainOfResCourse)
      throws Exception, IOException, JsonParseException, JsonMappingException {

      Set<String> domainsTaggedToResource = new HashSet<>();
      String query = GET_SIMILAR_CONTENT_WITH_STD_QUERY.replaceAll("SUBJECT_IDS",
          convertArrayToLWAndString(StringUtils.join(subjectArray, IndexerConstants.COMMA)));
      query = query.replaceAll("COURSE_IDS",
          convertArrayToLWAndString(StringUtils.join(courseArray, IndexerConstants.COMMA)));
      query = query.replaceAll("RESOURCE_ID", id);

      Response scSearchResponse = performRequest("POST",
          "/" + IndexNameHolder.getIndexName(EsIndex.RESOURCE) + "/" + IndexerConstants.TYPE_RESOURCE + "/_search",
          query);
      if (scSearchResponse.getEntity() != null) {
          Map<String, Object> scResponseAsMap =
              (Map<String, Object>) SERIAILIZER.readValue(EntityUtils.toString(scSearchResponse.getEntity()),
                  new TypeReference<Map<String, Object>>() {
                  });
          Map<String, Object> scHitsMap = (Map<String, Object>) scResponseAsMap.get("hits");
          Long scTotalHits = ((Integer) scHitsMap.get("total")).longValue();
          if (scTotalHits > 0) {
              Map<String, Object> aggsMap = (Map<String, Object>) scResponseAsMap.get("aggregations");
              Map<String, Object> gutsMap = (Map<String, Object>) aggsMap.get("domain_group");
              List<Map<String, Object>> gutsBucket = (List<Map<String, Object>>) gutsMap.get("buckets");
              gutsBucket.forEach(map -> {
                  String key = map.get("key").toString().toUpperCase();
                  for (String sa : subjectArray) {
                      if (key.startsWith(sa)) {
                          domainsTaggedToResource.add(key);
                      }
                  }
              });
          }
      }
      LOGGER.debug("allPossibleDomainOfResCourse : {}", allPossibleDomainOfResCourse);

      Set<String> domainsToRemove = new HashSet<>();
      for (Object adr : domainsTaggedToResource) {
          String ad = (String) adr;
          if (!allPossibleDomainOfResCourse.contains(ad)) {
              domainsToRemove.add(ad);
          }
      }
      domainsTaggedToResource.removeAll(domainsToRemove);

      LOGGER.debug("domainsTaggedToResource : {}", domainsTaggedToResource);
      Set<String> finalDomains = new HashSet<>();
      for (Object taggedDomain : domainsTaggedToResource) {
          String td = (String) taggedDomain;
          if (allPossibleDomainOfResCourse.contains(td)) {
              finalDomains.add(td);
          }
      }
      return finalDomains;
  }

}
