package org.gooru.nucleus.search.indexers.app.components;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;


public final class KafkaRegistry implements Initializer, Finalizer {

  private static final String DEFAULT_KAFKA_SETTINGS = "defaultKafkaSettings";
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRegistry.class);
  private Consumer<String, String> kafkaConsumer;

  private String KAFKA_TOPIC = "prodContentLog";
  private boolean testWithoutKafkaServer = false;

  private volatile boolean initialized = false;

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
          JsonObject kafkaConfig = config.getJsonObject(DEFAULT_KAFKA_SETTINGS);
          this.kafkaConsumer = initializeKafkaPublisher(kafkaConfig);
          initialized = true;
          LOGGER.debug("Initializing KafkaRegistry DONE");
        }
      }
    }
  }


  private Consumer<String, String> initializeKafkaPublisher(JsonObject kafkaConfig) {
    LOGGER.debug("InitializeKafkaConsumer now...");

    final Properties properties = new Properties();

    for (Map.Entry<String, Object> entry : kafkaConfig) {
      switch (entry.getKey()) {
        case ProducerConfig.BOOTSTRAP_SERVERS_CONFIG :
          properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.valueOf(entry.getValue()));
          LOGGER.debug("BOOTSTRAP_SERVERS_CONFIG : " + entry.getValue());
          break;
        case ProducerConfig.RETRIES_CONFIG:
          properties.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(entry.getValue()));
          LOGGER.debug("RETRIES_CONFIG : " +  entry.getValue());
          break;
        case ProducerConfig.BATCH_SIZE_CONFIG:
          properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(entry.getValue()));
          LOGGER.debug("BATCH_SIZE_CONFIG : " + entry.getValue());
          break;
        case ProducerConfig.LINGER_MS_CONFIG:
          properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, String.valueOf(entry.getValue()));
          LOGGER.debug("LINGER_MS_CONFIG : " +  entry.getValue());
          break;
        case ProducerConfig.BUFFER_MEMORY_CONFIG:
          properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, String.valueOf(entry.getValue()));
          LOGGER.debug("BUFFER_MEMORY_CONFIG : " + entry.getValue());
          break;
        case ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG:
          properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, String.valueOf(entry.getValue()));
          LOGGER.debug("KEY_SERIALIZER_CLASS_CONFIG : " + entry.getValue());
          break;
        case ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG:
          properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, String.valueOf(entry.getValue()));
          LOGGER.debug("VALUE_SERIALIZER_CLASS_CONFIG : " + entry.getValue());
          break;
        case "topic" :
          this.KAFKA_TOPIC = entry.getValue().toString();
          LOGGER.debug("KAFKA_TOPIC : " + this.KAFKA_TOPIC);
          break;
        case "testEnvironmentWithoutKafkaServer" :
          this.testWithoutKafkaServer = (boolean) entry.getValue();
          LOGGER.debug("KAFKA_TOPIC : " + this.KAFKA_TOPIC);
          break;
      }
    }

    if (this.testWithoutKafkaServer) return null;

    LOGGER.debug("InitializeKafkaConsumer properties created...");
    Consumer<String, String> consumer = new KafkaConsumer<>(properties);
    LOGGER.debug("InitializeKafkaConsumer consumer created successfully!");

    return consumer;
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

  public boolean testWithoutKafkaServer() {
    return this.testWithoutKafkaServer;
  }

  public String getKafkaTopic() {
    return this.KAFKA_TOPIC;
  }

  public static KafkaRegistry getInstance() {
    return Holder.INSTANCE;
  }

  private KafkaRegistry() {
    // TODO Auto-generated constructor stub
  }

  private static final class Holder {
    private static final KafkaRegistry INSTANCE = new KafkaRegistry();
  }

}
