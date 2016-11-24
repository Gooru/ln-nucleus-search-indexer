package org.gooru.nucleus.search.indexers.app.components;

import java.util.Properties;

import org.gooru.nucleus.search.indexers.app.constants.KafkaProperties;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.gooru.nucleus.search.indexers.kafka.consumer.KafkaNucleusConsumer;
import org.gooru.nucleus.search.indexers.kafka.consumer.KafkaKeywordConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

public final class KafkaRegistry implements Initializer, Finalizer {

  private static final String DEFAULT_KAFKA_SETTINGS = "defaultKafkaSettings";
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRegistry.class);
  private KafkaNucleusConsumer kafkaNucleusConsumer;
  private KafkaKeywordConsumer kafkaKeywordConsumer;

  private String KAFKA_NUCLEUS_TOPIC = null;
  private String KAFKA_KEYWORD_TOPIC = null;

  private volatile boolean initialized = false;

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
            Properties properties = getProperties(kafkaConfig);
            initializeNucleusConsumer(properties);
            initializeKeywordConsumer(properties);
            initialized = true;
            LOGGER.debug("Initializing KafkaRegistry DONE");
          } catch (Exception e) {
            LOGGER.error("Initializing KafkaRegistry Failed " + e);
          }
        }
      }
    }
  }

  private Properties getProperties(JsonObject kafkaConfig) {
    LOGGER.debug("Setting Kafka Properties..");
    final Properties properties = new Properties();
    properties.put(KafkaProperties.ZK_CONSUMER_CONNECT, kafkaConfig.getString(KafkaProperties.ZK_CONSUMER_CONNECT));
    properties.put(KafkaProperties.ZK_CONSUMER_GROUP, kafkaConfig.getString(KafkaProperties.ZK_CONSUMER_GROUP));
    properties.put(KafkaProperties.ZK_SESSION_TIME_OUT_MS, kafkaConfig.getString(KafkaProperties.ZK_SESSION_TIME_OUT_MS));
    properties.put(KafkaProperties.ZK_SYNCTIME_MS, kafkaConfig.getString(KafkaProperties.ZK_SYNCTIME_MS));
    properties.put(KafkaProperties.AUTOCOMMIT_INTERVAL_MS, kafkaConfig.getString(KafkaProperties.AUTOCOMMIT_INTERVAL_MS));
    properties.put(KafkaProperties.FETCH_SIZE, kafkaConfig.getString(KafkaProperties.FETCH_SIZE));
    properties.put(KafkaProperties.AUTO_OFFSET_RESET, kafkaConfig.getString(KafkaProperties.AUTO_OFFSET_RESET));

    this.KAFKA_NUCLEUS_TOPIC = kafkaConfig.getString(KafkaProperties.INDEX_NUCLEUS_TOPIC);
    this.KAFKA_KEYWORD_TOPIC = kafkaConfig.getString(KafkaProperties.INDEX_KEYWORD_TOPIC);
    return properties;
  }

  private void initializeNucleusConsumer(final Properties properties) {
    LOGGER.debug("initializeDefaultConsumer now...");
    kafkaNucleusConsumer = createNucleusConsumer(properties);
    kafkaNucleusConsumer.start(getKafkaNucleusTopic());
  }

  private void initializeKeywordConsumer(final Properties properties) {
    LOGGER.debug("initializeKeywordConsumer now...");
    kafkaKeywordConsumer = createKeywordConsumer(properties);
    kafkaKeywordConsumer.start(getKafkaKeywordTopic());
  }

  public static KafkaNucleusConsumer createNucleusConsumer(final Properties properties) {
    final ConsumerConfig config = new ConsumerConfig(properties);
    final ConsumerConnector connector = Consumer.createJavaConsumerConnector(config);
    return new KafkaNucleusConsumer(connector);
  }
  
  public static KafkaKeywordConsumer createKeywordConsumer(final Properties properties) {
    final ConsumerConfig config = new ConsumerConfig(properties);
    final ConsumerConnector connector = Consumer.createJavaConsumerConnector(config);
    return new KafkaKeywordConsumer(connector);
  }

  @Override
  public void finalizeComponent() {
    if (this.kafkaNucleusConsumer != null) {
      KafkaNucleusConsumer.shutdownMessageConsumer();;
      this.kafkaNucleusConsumer = null;
    }
    if (this.kafkaKeywordConsumer != null) {
      KafkaKeywordConsumer.shutdownMessageConsumer();;
      this.kafkaKeywordConsumer = null;
    }
  }

  public String getKafkaNucleusTopic() {
    return this.KAFKA_NUCLEUS_TOPIC;
  }
  
  public String getKafkaKeywordTopic() {
    return this.KAFKA_KEYWORD_TOPIC;
  }

  private static final class Holder {
    private static final KafkaRegistry INSTANCE = new KafkaRegistry();
  }

}
