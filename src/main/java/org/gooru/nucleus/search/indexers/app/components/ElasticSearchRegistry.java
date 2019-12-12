package org.gooru.nucleus.search.indexers.app.components;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.gooru.nucleus.search.indexers.app.constants.ElasticSearchConnectionConstant;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.utils.EsMappingUtil;
import org.gooru.nucleus.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.util.StringUtil;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This class initializes ElasticSearch client and registers indices using setting/ mapping files
 *
 * @author Renuka
 */
public final class ElasticSearchRegistry implements Finalizer, Initializer {

  protected static final ObjectMapper SERIAILIZER = new ObjectMapper();
  private static final String DEFAULT_ELASTIC_SETTINGS = "defaultElasticSettings";
  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchRegistry.class);
  private volatile boolean initialized = false;
  private RestClient restClient = null;
  private RestHighLevelClient restHighLevelClient = null;

  private Sniffer sniffer = null;

  private String indexNamePrefix;

  private String indexNameSuffix;

  public ElasticSearchRegistry() {
  }

  public static ElasticSearchRegistry getInstance() {
    return Holder.INSTANCE;
  }

  public static ElasticSearchRegistry getFactory() {
    return ElasticSearchRegistry.getInstance();
  }

  @Override
  public void initializeComponent(Vertx vertx, JsonObject config) {
    // Skip if we are already initialized
    LOGGER.debug("Initialization called upon.");
    if (!initialized) {
      LOGGER.debug("May have to do initialization");
      synchronized (Holder.INSTANCE) {
        LOGGER.debug("Will initialize after double checking");
        if (!initialized) {
          LOGGER.info("Initializing ELS Registry now");

          JsonObject elasticSearchConfig = config.getJsonObject(DEFAULT_ELASTIC_SETTINGS);
          initializeElasticSearchClient(elasticSearchConfig);
          if (getFactory().restClient != null) {
            registerIndices();
            initialized = true;
            LOGGER.info("Initialize ELS Registry DONE");
          } else {
            LOGGER.info("Initialize ELS Registry FAILED");
          }
        }
      }
    }
  }

  private void initializeElasticSearchClient(JsonObject elasticSearchConfig) {

    String hostName =
            elasticSearchConfig.getString(ElasticSearchConnectionConstant.HOST.getKey(), ElasticSearchConnectionConstant.HOST.getDefaultValue());
    String indexPrefixPart = elasticSearchConfig.getString(ElasticSearchConnectionConstant.INDEX_PREFIX_PART.getKey(),
            ElasticSearchConnectionConstant.INDEX_PREFIX_PART.getDefaultValue());
    String indexMiddlePart = elasticSearchConfig.getString(ElasticSearchConnectionConstant.INDEX_MIDDLE_PART.getKey(),
            ElasticSearchConnectionConstant.INDEX_MIDDLE_PART.getDefaultValue());
    String indexSuffixPart = elasticSearchConfig.getString(ElasticSearchConnectionConstant.INDEX_SUFFIX_PART.getKey(),
            ElasticSearchConnectionConstant.INDEX_SUFFIX_PART.getDefaultValue());
    
    setIndexNamePrefix(indexPrefixPart + indexMiddlePart);
    setIndexNameSuffix(indexSuffixPart);

    HttpHost[] httpHosts = buildHosts(hostName);
    SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
    RestClient restClient = RestClient.builder(httpHosts).setFailureListener(sniffOnFailureListener).build();
    RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);
    Sniffer sniffer = Sniffer.builder(restClient).setSniffAfterFailureDelayMillis(30000).build();
    sniffOnFailureListener.setSniffer(sniffer);
    
    setLowLevelRestClient(restClient);
    setRestHighLevelClient(restClientBuilder);
    setSniffer(sniffer);
  }

  private void registerIndices() {

    for (EsIndex esIndex : EsIndex.values()) {
      String indexName = getIndexNamePrefix() + esIndex.getName() + getIndexNameSuffix();
      IndexNameHolder.registerIndex(esIndex, indexName);
      LOGGER.debug("Registering : {} Index", indexName);
      for (String indexType : esIndex.getTypes()) {
        LOGGER.debug("Es Index Type  : {}", indexType);

        String indexSettings = EsMappingUtil.getIndexSettingsConfig(esIndex.getName());
        try {
          performRequest("PUT", "/" + indexName + "?include_type_name=false", indexSettings);
          LOGGER.debug("Es Index : {} Created!", indexName);
        } catch (Exception exception) {
          if (exception instanceof ResponseException) {
            if (((ResponseException) exception).getResponse().getStatusLine().getStatusCode() == 400
                    && (exception.getMessage().contains("index_already_exists_exception") || // ES 5.x
                            exception.getMessage().contains("resource_already_exists_exception") || // ES 6.x
                            exception.getMessage().contains("IndexAlreadyExistsException")|| // ES 6.x
                            exception.getMessage().contains("invalid_index_name_exception"))) {
              LOGGER.debug("Oops! Es Index : {} already exist!", indexName);
              Response updateResponse;
              try {
                String mapping = EsMappingUtil.getMappingConfig(indexType);
                updateResponse = performRequest("PUT", "/" + indexName + "/_mapping?include_type_name=false", mapping);
                if (updateResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                  LOGGER.debug("Updated mapping with index '{}' and type '{}'", indexName, indexType);
                }
              } catch (Exception e) {
                LOGGER.error("Update mapping failed : {}", e);
              }
            } else {
              LOGGER.debug("Register index failed! Reason : {}", exception);
            }
          } else {
            LOGGER.debug("Index creation failed! Reason : {}", exception);
          }
        }
      }
    }
  }

  private Response performRequest(String method, String indexUrl, String indexSettings) throws Exception {
    Request request = new Request(method, indexUrl);
    StringEntity entity = null;
    if (!StringUtil.isNullOrEmpty(indexSettings)) {
      entity = new StringEntity(indexSettings, ContentType.APPLICATION_JSON);
      request.setEntity(entity);
    }
    Response response = getLowLevelRestClient().performRequest(request);
    return response;
  }
  
  private HttpHost[] buildHosts(String endpoints) {
    String[] hosts = endpoints.split(",");
    HttpHost[] httpHosts = new HttpHost[0];
    for (String host : hosts) {
      String[] hostParams = host.split(":");
      if (hostParams.length == 2) {
        httpHosts = appendToArray(httpHosts , new HttpHost(hostParams[0], Integer.valueOf(hostParams[1]), "http"));
        LOGGER.debug("Host added : {} to elasticsearch!", host);
      } else {
        LOGGER.debug("Oops! Could't initialize rest client with host : {}", host);
      }
    }
    return httpHosts;
  }
  
  private HttpHost[] appendToArray(HttpHost[] array, HttpHost x){
    HttpHost[] result = new HttpHost[array.length + 1];
      for(int i = 0; i < array.length; i++)
          result[i] = array[i];
      result[result.length - 1] = x;
      return result;
  }

  @Override
  public void finalizeComponent() {
    if (getLowLevelRestClient() != null) {
      try {
        getLowLevelRestClient().close();
        getSniffer().close();
        LOGGER.info("Rest client shutdown");
      } catch (IOException e) {
        LOGGER.info("Rest Client is not shutdown : {}", e);
      }
    }
  }

  public static RestClient getLowLevelRestClient() {
    return getFactory().restClient;
  }

  public static void setLowLevelRestClient(RestClient client) {
    getFactory().restClient = client;
  }
  
  public static RestHighLevelClient getRestHighLevelClient() {
    return getFactory().restHighLevelClient;
  }

  public static void setRestHighLevelClient(RestClientBuilder clientBuilder) {
    getFactory().restHighLevelClient = new RestHighLevelClient(clientBuilder);
  }
  
  public static Sniffer getSniffer() {
    return getFactory().sniffer;
  }

  public static void setSniffer(Sniffer sniffer) {
    getFactory().sniffer = sniffer;
  }

  public String getIndexNamePrefix() {
    return indexNamePrefix;
  }

  private void setIndexNamePrefix(String indexPrefixName) {
    this.indexNamePrefix = indexPrefixName;
  }

  public String getIndexNameSuffix() {
    return indexNameSuffix;
  }

  public void setIndexNameSuffix(String indexNameSuffix) {
    this.indexNameSuffix = indexNameSuffix;
  }

  private static final class Holder {
    private static final ElasticSearchRegistry INSTANCE = new ElasticSearchRegistry();
  }

}
