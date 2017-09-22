package org.gooru.nucleus.search.indexers.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.util.StringUtil;

import io.vertx.core.json.JsonObject;

public class KafkaMessageConsumer implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageConsumer.class);
  private KafkaConsumer<String, String> consumer = null;
  
  public KafkaMessageConsumer(KafkaConsumer<String, String> consumer) {
    this.consumer = consumer;
  }

  @Override
  public void run() {
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(200);
      for (ConsumerRecord<String, String> record : records) {
        if (!StringUtil.isNullOrEmpty(record.value())) {
          JsonObject eventObject = null;
          try {
            LOGGER.info("Message : {}", record.value());
            eventObject = new JsonObject(record.value());
          } catch (Exception e) {
            LOGGER.warn("Re-index failed : Kafka Message should be JsonObject");
          }
          if (eventObject != null) {
            ProcessorBuilder.build(eventObject).process();
          }
        } else {
          LOGGER.warn("NULL or Empty message can not be processed...");
        }
      }
    }
  }
}
