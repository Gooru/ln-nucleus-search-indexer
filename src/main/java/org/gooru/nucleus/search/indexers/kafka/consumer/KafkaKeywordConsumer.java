package org.gooru.nucleus.search.indexers.kafka.consumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.gooru.nucleus.search.indexers.app.processors.ProcessorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.vertx.core.json.JsonObject;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class KafkaKeywordConsumer {

  private final static Logger LOG = LoggerFactory.getLogger(KafkaKeywordConsumer.class);

  private final static ThreadFactory FACTORY = new ThreadFactoryBuilder().setNameFormat("kafka-consumer-thread-%d").setDaemon(true).build();
  private static ConsumerConnector consumer;
  private final ExecutorService messageProcessorExecutor = Executors.newSingleThreadExecutor(FACTORY);
  
  public KafkaKeywordConsumer(ConsumerConnector connector) {
    KafkaKeywordConsumer.consumer = connector;
  }

  public void stop() {
    messageProcessorExecutor.shutdownNow();
    consumer.shutdown();
  }

  public void start(final String kafkaTopic) {
    final Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams =
            KafkaKeywordConsumer.consumer.createMessageStreams(ImmutableMap.of(kafkaTopic, 1));
    final List<KafkaStream<byte[], byte[]>> topicStreams = messageStreams.get(kafkaTopic);
    final KafkaStream<byte[], byte[]> topicStream = Iterables.getOnlyElement(topicStreams);

    messageProcessorExecutor.submit(() -> read(topicStream));
  }

  private void read(final KafkaStream<byte[], byte[]> stream) {
    while (stream.iterator().hasNext()) {
      try {
        final MessageAndMetadata<byte[], byte[]> msg = stream.iterator().next();
       // String key = msg.key();
        String message = new String(msg.message());
       // LOG.debug("key : " + key + " Index message :" + message);
        LOG.info("Continuing message processing");
        ProcessorBuilder.build(new JsonObject(message)).process();
      } catch (Exception e) {
        LOG.error("Re-index failed " + e);
      }
    }
  }
  
  /**
   * Clean Shutdown
   */
  public static void shutdownMessageConsumer() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      LOG.debug("Kafka Log Consumer unable to wait for 1000ms before it's shutdown");
    }
    consumer.shutdown();
  }

}
