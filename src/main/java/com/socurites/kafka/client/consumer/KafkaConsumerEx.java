package com.socurites.kafka.client.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerEx {
  private static final Logger log = LoggerFactory.getLogger(KafkaConsumerEx.class);

  @KafkaListener(topics = "#{'${app.kafka.topic}'}", concurrency = "2")
  public void consume(final String message) {
    log.info("Received message: {}", message);
  }
}
