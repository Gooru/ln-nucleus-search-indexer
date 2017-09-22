package org.gooru.nucleus.search.indexers.app.components;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.gooru.nucleus.search.indexers.app.constants.KafkaProperties;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.gooru.nucleus.search.indexers.kafka.consumer.KafkaMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public final class KafkaRegistry implements Initializer, Finalizer {

  private static final String DEFAULT_KAFKA_SETTINGS = "defaultKafkaSettings";
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRegistry.class);
  private KafkaConsumer<String, String> consumer = null;

  private String[] KAFKA_TOPICS = null;
  private static ExecutorService service = Executors.newFixedThreadPool(10);


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
            createConsumer(properties);
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
    properties.put(KafkaProperties.KAFKA_SERVERS, kafkaConfig.getString(KafkaProperties.KAFKA_SERVERS));
    properties.put(KafkaProperties.SESSION_TIME_OUT_MS, kafkaConfig.getString(KafkaProperties.SESSION_TIME_OUT_MS));
    properties.put(KafkaProperties.GROUP_ID, kafkaConfig.getString(KafkaProperties.GROUP_ID));
    
    properties.put(KafkaProperties.KAFKA_KEY_DESERIALIZER, StringDeserializer.class.getName());
    properties.put(KafkaProperties.KAFKA_VALUE_DESERIALIZER, StringDeserializer.class.getName());

    this.KAFKA_TOPICS = kafkaConfig.getString(KafkaProperties.KAFKA_TOPICS).split(",");
    return properties;
  }

  public void createConsumer(final Properties properties) {
    consumer = new KafkaConsumer<>(properties);
    consumer.subscribe(Arrays.asList(KAFKA_TOPICS));
    service.submit(new KafkaMessageConsumer(consumer));
  }

  @Override
  public void finalizeComponent() {
    if (this.consumer != null) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        LOGGER.debug("Kafka Log Consumer unable to wait for 1000ms before it's shutdown");
      }
      consumer.close();
    }
  }

  private static final class Holder {
    private static final KafkaRegistry INSTANCE = new KafkaRegistry();
  }

}
