package com.aml_service.client;

import com.aml_service.model.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    final String prefix = "[Producer]";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties props;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate,
                         KafkaProperties props) {
        this.kafkaTemplate = kafkaTemplate;
        this.props = props;
    }

    public void publish(Object payload) {
        kafkaTemplate.send(props.getOutboundTopic(), payload);
    }

    public void publishTest(String topic, Transaction payload) {
        kafkaTemplate.send(topic, payload);
    }
}
