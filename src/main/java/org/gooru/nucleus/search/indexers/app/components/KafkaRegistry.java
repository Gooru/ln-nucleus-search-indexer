package org.gooru.nucleus.search.indexers.app.components;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.gooru.nucleus.search.indexers.app.constants.KafkaProperties;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.gooru.nucleus.search.indexers.kafka.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public final class KafkaRegistry implements Initializer, Finalizer {

  private static final String DEFAULT_KAFKA_SETTINGS = "defaultKafkaSettings";
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRegistry.class);
  private Consumer<String, String> kafkaConsumer;

  private String KAFKA_TOPIC = "prodIndex";

  private volatile boolean initialized = false;

  private KafkaRegistry() {
    // TODO Auto-generated constructor stub
  }

  public static KafkaRegistry getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public void initializeComponent(Vertx vertx, JsonObject config) {
    // Skip if we are already initialized
    LOGGER.debug("Initialization called upon.");
    if (!initialized) {
      LOGGER.debug("May have to do initialization");
      // We need to do initialization, however, we are running it via verticle instance which is going to run in
      // multiple threads hence we need to be safe for this operation
      synchronized (Holder.INSTANCE) {
        LOGGER.debug("Will initialize after double checking");
        if (!initialized) {
          LOGGER.debug("Initializing KafkaRegistry now");
          try {
            JsonObject kafkaConfig = config.getJsonObject(DEFAULT_KAFKA_SETTINGS);
            initializeKafkaConsumer(kafkaConfig);
            initialized = true;
            LOGGER.debug("Initializing KafkaRegistry DONE");
          } catch (Exception e) {
            LOGGER.error("Initializing KafkaRegistry Failed " + e);
          }
        }
      }
    }
  }

  private void initializeKafkaConsumer(JsonObject kafkaConfig) {
    LOGGER.debug("InitializeKafkaConsumer now...");

    final Properties properties = new Properties();

    properties.put(KafkaProperties.ZK_CONSUMER_CONNECT, kafkaConfig.getString(KafkaProperties.ZK_CONSUMER_CONNECT));
    properties.put(KafkaProperties.ZK_CONSUMER_GROUP, kafkaConfig.getString(KafkaProperties.ZK_CONSUMER_GROUP));
    properties.put(KafkaProperties.ZK_SESSION_TIME_OUT_MS, kafkaConfig.getString(KafkaProperties.ZK_SESSION_TIME_OUT_MS));
    properties.put(KafkaProperties.ZK_SYNCTIME_MS, kafkaConfig.getString(KafkaProperties.ZK_SYNCTIME_MS));
    properties.put(KafkaProperties.AUTOCOMMIT_INTERVAL_MS, kafkaConfig.getString(KafkaProperties.AUTOCOMMIT_INTERVAL_MS));
    properties.put(KafkaProperties.FETCH_SIZE, kafkaConfig.getString(KafkaProperties.FETCH_SIZE));
    properties.put(KafkaProperties.AUTO_OFFSET_RESET, kafkaConfig.getString(KafkaProperties.AUTO_OFFSET_RESET));

    this.KAFKA_TOPIC = kafkaConfig.getString(KafkaProperties.INDEX_TOPIC);
    KafkaConsumer consumer = KafkaConsumer.create(properties);
    consumer.start(KAFKA_TOPIC);
  }

  public Consumer<String, String> getKafkaConsumer() {
    if (initialized) {
      return this.kafkaConsumer;
    }
    return null;
  }

  @Override
  public void finalizeComponent() {
    if (this.kafkaConsumer != null) {
      this.kafkaConsumer.close();
      this.kafkaConsumer = null;
    }
  }

  public String getKafkaTopic() {
    return this.KAFKA_TOPIC;
  }

  private static final class Holder {
    private static final KafkaRegistry INSTANCE = new KafkaRegistry();
  }

}
