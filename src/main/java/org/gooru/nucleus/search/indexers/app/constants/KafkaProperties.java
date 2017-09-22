package org.gooru.nucleus.search.indexers.app.constants;

public final class KafkaProperties {

  //Kafka Config Constants
  public static final String GROUP_ID = "group.id";
  public static final String SESSION_TIME_OUT_MS = "session.timeout.ms";
  public static final String AUTOCOMMIT_INTERVAL_MS = "auto.commit.interval.ms";
  public static final String AUTO_OFFSET_RESET = "auto.offset.reset";
  public static final String KAFKA_SERVERS = "bootstrap.servers";
  public static final String KAFKA_TOPICS = "consumer.topics";
  public static final String KAFKA_KEY_DESERIALIZER = "key.deserializer";
  public static final String KAFKA_VALUE_DESERIALIZER = "value.deserializer";
  public static final String THREAD_POOL_SIZE ="thead.pool.size";
  public static final String ENABLE_AUTO_COMMIT = "enable.auto.commit";

  private KafkaProperties() {
    throw new AssertionError();
  }
}
