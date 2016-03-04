package org.gooru.nucleus.search.indexers.app.components;

import java.net.InetAddress;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.gooru.nucleus.search.indexers.app.constants.ElasticSearchConstant;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.gooru.nuclues.search.indexers.app.utils.EsMappingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This class initializes Jest client for ElasticSearch and registers indices using setting/ mapping files
 *
 * @author Renuka
 */
public class ElasticSearchClient implements Finalizer, Initializer {

	private static final String DEFAULT_ELASTIC_SETTINGS = "defaultElasticSettings";
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchClient.class);
	private volatile boolean initialized = false;
	private Client client;

	private String indexNamePrefix;
	
	private String indexNameSuffix;
	
	public static ElasticSearchClient getInstance() {
		return Holder.INSTANCE;
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
					registerIndices();
					initialized = true;
					LOGGER.info("Initializing ELS Registry DONE");
				}
			}
		}
	}

	private void initializeElasticSearchClient(JsonObject elasticSearchConfig) {

		String clusterName = elasticSearchConfig.getString(ElasticSearchConstant.CLUSTER_NAME.getKey(), ElasticSearchConstant.CLUSTER_NAME.getDefaultValue());
		String hostName = elasticSearchConfig.getString(ElasticSearchConstant.HOST.getKey(), ElasticSearchConstant.HOST.getDefaultValue());
		String indexPrefixPart = elasticSearchConfig.getString(ElasticSearchConstant.INDEX_PREFIX_PART.getKey(), ElasticSearchConstant.INDEX_PREFIX_PART.getDefaultValue());
		String indexMiddlePart = elasticSearchConfig.getString(ElasticSearchConstant.INDEX_MIDDLE_PART.getKey(), ElasticSearchConstant.INDEX_MIDDLE_PART.getDefaultValue());
		String indexSuffixPart = elasticSearchConfig.getString(ElasticSearchConstant.INDEX_SUFFIX_PART.getKey(), ElasticSearchConstant.INDEX_SUFFIX_PART.getDefaultValue());
		String clientTransportSniff = elasticSearchConfig.getString(ElasticSearchConstant.CLIENT_TRANSPORT_SNIFF.getKey(), ElasticSearchConstant.CLIENT_TRANSPORT_SNIFF.getDefaultValue());

		setIndexNamePrefix(indexPrefixPart + indexMiddlePart);
		setIndexNameSuffix(indexSuffixPart);

		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", clusterName)
				.put("client.transport.sniff", Boolean.valueOf(clientTransportSniff)).build();
		LOGGER.debug("ELS Cluster Name : " + clusterName);
		TransportClient transportClient = TransportClient.builder().settings(settings).build();

		String[] hosts = hostName.split(",");
		for (String host : hosts) {
			String[] hostParams = host.split(":");
			if (hostParams.length == 2) {
				try {
					LOGGER.debug("host : " + hostParams[0] + " port : " + hostParams[1]);
					transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostParams[0]), Integer.parseInt(hostParams[1])));
				} catch (Exception e) {
					LOGGER.error("Add transport address failed : " , e);
				}
				LOGGER.debug("Host added : " + host + " to elasticsearch!");
			} else {
				LOGGER.debug("Oops! Could't add host : " + host + " to elasticsearch!");
			}
		}
		setClient(transportClient);

	}

	private final void registerIndices() {
		for (EsIndex esIndex : EsIndex.values()) {
			String indexName = getIndexNamePrefix() + esIndex.getName() + getIndexNameSuffix();
			LOGGER.debug("Registering : " + indexName + " Index");
			for (String indexType : esIndex.getTypes()) {
				LOGGER.debug("Es Index Type  : " + indexType);

				String setting = EsMappingUtil.getSettingConfig(indexType);
				String mapping = EsMappingUtil.getMappingConfig(indexType);
				try {
					CreateIndexRequestBuilder prepareCreate = ElasticSearchClient.getInstance().getClient().admin().indices().prepareCreate(indexName);
					prepareCreate.setSettings(setting);
					prepareCreate.addMapping(indexType, mapping);
					prepareCreate.execute().actionGet();
					LOGGER.debug("Es Index : " + indexName + " Created!");
				} catch (Exception exception) {
					if (exception instanceof IndexAlreadyExistsException) {
						LOGGER.debug("Oops! Es Index : " + indexName + " already exist!");
						ElasticSearchClient.getInstance().getClient().admin().indices().preparePutMapping(indexName).setType(indexType).setSource(mapping).execute().actionGet();
						LOGGER.debug("Updated mapping with index '" + indexName + "' and type '" + indexType + "'");
					} else {
						LOGGER.error("Register index failed : ", exception);
					}
				}
			}
		}
	}
	
	@Override
	public void finalizeComponent() {
		if (client != null) {
			client.close();
		}
	}

	public Client getClient() {
		return getFactory().client;
	}

	private void setClient(Client client) {
		getFactory().client = client;
	}
	
	public static ElasticSearchClient getFactory() {
		return ElasticSearchClient.getInstance();
	}
	
	public ElasticSearchClient() {}

	public String getIndexNamePrefix() {
		return indexNamePrefix;
	}

	//TODO Add logic to fetch config from property file, if not available in cassandra 
	/*private String getSetting(ElasticsearchConstant constant) {
		String value = configSettingRepository != null ? configSettingRepository.getSetting(constant.getKey()) : constant.getDefaultValue();
		return value != null && value.length() > 0 ? value : constant.getDefaultValue();
	}*/
	
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
		private static final ElasticSearchClient INSTANCE = new ElasticSearchClient();
	}

}
