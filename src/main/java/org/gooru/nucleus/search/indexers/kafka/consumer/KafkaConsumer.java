
package org.gooru.nucleus.search.indexers.kafka.consumer;

import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;

public class KafkaConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

    private final static ThreadFactory FACTORY = new ThreadFactoryBuilder().setNameFormat("kafka-consumer-thread-%d").setDaemon(true).build();
    private final ConsumerConnector connector;
    private final ExecutorService messageProcessorExececutor = Executors.newSingleThreadExecutor(FACTORY);

    public KafkaConsumer(ConsumerConnector connector) {
        this.connector = connector;
    }

    public static KafkaConsumer create(final Properties properties) {
        final ConsumerConfig config = new ConsumerConfig(properties);
        final ConsumerConnector connector = Consumer.createJavaConsumerConnector(config);
        return new KafkaConsumer(connector);
    }

     public void stop() {
        messageProcessorExececutor.shutdownNow();
        connector.shutdown();
    }

    public void start(final String kafkaTopic) {
        final Map<String, List<KafkaStream<String, String>>> messageStreams = connector.createMessageStreams(ImmutableMap.of(kafkaTopic, 1),
                new StringDecoder(null),
                new StringDecoder(null));
        final List<KafkaStream<String, String>> topicStreams = messageStreams.get(kafkaTopic);
        final KafkaStream<String, String> topicStream = Iterables.getOnlyElement(topicStreams);

        messageProcessorExececutor.submit(() -> read(topicStream));
    }

    private void read(final KafkaStream<String, String> stream) {
        while (stream.iterator().hasNext()) {
          try{
            final MessageAndMetadata<String, String> msg = stream.iterator().next();
            String key = msg.key(); 
            String message = msg.message();   
            LOG.debug("key : " + key + " Index message :" + message);
            LOG.info("Continuing message processing");
            ProcessorBuilder.build(new JsonObject(message)).process();
          }
          catch(Exception e){
            LOG.error("Re-index failed " + e);
          }
        }
    }

}
