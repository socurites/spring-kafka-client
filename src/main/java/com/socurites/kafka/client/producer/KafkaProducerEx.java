package com.socurites.kafka.client.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class KafkaProducerEx {
  private static final Logger log = LoggerFactory.getLogger(KafkaProducerEx.class);

  private final String topicName;

  private final KafkaTemplate<String, String> kafkaTemplate;

  public KafkaProducerEx(@Value("${app.kafka.topic}") String topicName,
                         KafkaTemplate<String, String> kafkaTemplate) {
    this.topicName = topicName;
    this.kafkaTemplate = kafkaTemplate;
  }

  @EventListener(ApplicationStartedEvent.class)
  public void produce() {
    IntStream.range(0, 10).forEach(i ->
      send(i));

    kafkaTemplate.flush();
  }

  private void send(int i) {
    log.info(String.format("Sending %d messages", i));

    kafkaTemplate.send(topicName, String.format("messages-%d", i))
      .addCallback(new KafkaSendCallback<String, String>() {
        @Override
        public void onSuccess(SendResult<String, String> result) {
          RecordMetadata recordMetadata = result.getRecordMetadata();
          log.info("Produced record to topic {}, partition {}, @ offset {}",
            recordMetadata.topic(),
            recordMetadata.partition(),
            recordMetadata.offset());
        }

        @Override
        public void onFailure(Throwable ex) {
          log.error(ex.getMessage(), ex);
          KafkaSendCallback.super.onFailure(ex);
        }

        @Override
        public void onFailure(KafkaProducerException ex) {
          log.error(ex.getMessage(), ex);
        }
      });
  }
}
