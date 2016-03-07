package org.gooru.nucleus.search.indexers.app.components;

import org.gooru.nucleus.search.indexers.app.constants.CassandraConstant;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This class initializes Astyanax client for Cassandra and registers Keyspace
 *
 * @author Renuka
 */
public class CassandraClient implements Finalizer, Initializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CassandraClient.class);
	private static final String DEFAULT_CASSANDRA_SETTINGS = "defaultCassandraSettings";
	private static final String CONNECTION_POOL = "MyConnectionPool";
	private volatile boolean initialized = false;
	private Keyspace keyspace;
	private AstyanaxContext<Keyspace> astyanaxContext;

	@Override
	public void initializeComponent(Vertx vertx, JsonObject config) {
		// Skip if we are already initialized
		LOGGER.debug("Initialization called upon.");
		if (!initialized) {
			LOGGER.debug("May have to do initialization");
			synchronized (Holder.INSTANCE) {
				LOGGER.debug("Will initialize after double checking");
				if (!initialized) {
					LOGGER.info("Initializing Cassandra Registry now");
					
					JsonObject cassandraConfig = config.getJsonObject(DEFAULT_CASSANDRA_SETTINGS);
					try {
						this.astyanaxContext = getCassandraContext(cassandraConfig);
						this.astyanaxContext.start();
						this.keyspace = this.astyanaxContext.getClient();
						createKeySpaceIfNotExist(cassandraConfig);
						this.keyspace.describeKeyspace();
						initialized = true;
						LOGGER.info("Initializing Cassandra Registry DONE");
					} catch (Throwable e) {
						LOGGER.info("Initializing Cassandra Registry FAILED");
						LOGGER.error("Could not connect to cassandra : ", e);
					}
				}
			}
		}
	}
	
	protected void createKeySpaceIfNotExist(JsonObject cassandraConfig) throws ConnectionException {
		try {
			this.keyspace.describeKeyspace();
		} catch (Exception ex) {
			String 	strategyOption = cassandraConfig.getString(CassandraConstant.STRATEGY_OPTIONS.getKey(), CassandraConstant.STRATEGY_OPTIONS.getDefaultValue());
			String 	strategyClass = cassandraConfig.getString(CassandraConstant.STRATEGY_CLASS.getKey(), CassandraConstant.STRATEGY_CLASS.getDefaultValue());			

			String[] strategyOptions = strategyOption.split(",");
			ImmutableMap.Builder<String, Object> strategyMap = ImmutableMap.<String, Object> builder();
			for (String strategy : strategyOptions) {
				String[] params = strategy.split("=");
				strategyMap.put(params[0], params[1]);
			}
			keyspace.createKeyspace(ImmutableMap.<String, Object> builder().put("strategy_options", strategyMap.build()).put("strategy_class", strategyClass).build());
			LOGGER.warn("Cassandra Keyspace : " + keyspace.getKeyspaceName() + " doesn't exist, Created!");
		}
	}

	protected AstyanaxContext<Keyspace> getCassandraContext(JsonObject cassandraConfig) {
		String clusterName = cassandraConfig.getString(CassandraConstant.CLUSTER_NAME.getKey(), CassandraConstant.CLUSTER_NAME.getDefaultValue());
		String cqlVersion = cassandraConfig.getString(CassandraConstant.CQL_VERSION.getKey(), CassandraConstant.CQL_VERSION.getDefaultValue());
		String version = cassandraConfig.getString(CassandraConstant.VERSION.getKey(), CassandraConstant.VERSION.getDefaultValue());
		String seed = cassandraConfig.getString(CassandraConstant.SEED.getKey(), CassandraConstant.SEED.getDefaultValue());
		return new AstyanaxContext.Builder()
				.forCluster(clusterName)
				.forKeyspace(getKeyspaceName(cassandraConfig))
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl()
						.setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
						.setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE)
						.setCqlVersion(cqlVersion)
						.setTargetCassandraVersion(version))
				.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl(CONNECTION_POOL)
				.setMaxConnsPerHost(100)
				.setSeeds(seed))
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
				.buildKeyspace(ThriftFamilyFactory.getInstance());
	}
	
	protected String getKeyspaceName(JsonObject cassandraConfig) {
		String keyspacePrefix = cassandraConfig.getString(CassandraConstant.KEYSPACE_NAME_PREFIX.getKey(), CassandraConstant.KEYSPACE_NAME_PREFIX.getDefaultValue());
		String keyspaceSuffix = cassandraConfig.getString(CassandraConstant.KEYSPACE_NAME_SUFFIX.getKey(), CassandraConstant.KEYSPACE_NAME_SUFFIX.getDefaultValue());
		return keyspacePrefix + "_" + (keyspaceSuffix.equals(keyspacePrefix) ? CassandraConstant.KEYSPACE_NAME_SUFFIX.getDefaultValue() : keyspaceSuffix);
	}

	@Override
	public void finalizeComponent() {
		if (astyanaxContext != null) {
			astyanaxContext.shutdown();
		}
	}

	public static CassandraClient getInstance() {
		return Holder.INSTANCE;
	}

	private CassandraClient() {
	}
	
	public Keyspace getKeyspace() {
		return keyspace;
	}

	private static final class Holder {
		private static final CassandraClient INSTANCE = new CassandraClient();
	}

}
