package com.aml_service.client;

import com.aml_service.model.Transaction;
import com.aml_service.model.TransactionType;
import com.aml_service.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    final String prefix = "[Consumer]";
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper mapper;
    private final TransactionService service;

    public KafkaConsumer(ObjectMapper mapper,
                         TransactionService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @KafkaListener(topics = "${kafka.inbound-topic}", groupId = "${kafka.group-id}")
    public void listen(String message, Acknowledgment acknowledgment) {
        try {
            if (message == null || message.isEmpty()) {
                return;
            }
            logger.info("{} Message received the process will start: {}", prefix, message);
            Transaction trx = mapper.readValue(message, Transaction.class);

            if (!TransactionType.OUTBOUND.name().equalsIgnoreCase(trx.getType())) {
                return;
            }

            service.processTransaction(trx);
            acknowledgment.acknowledge();
            logger.info("{} Logic passed, acknowledgment given for message: {}", prefix, message);
        } catch (Exception e) {
            logger.error("{} Error, no acknowledgment given for message: {}", prefix, message);
            throw new RuntimeException(e);
        }
    }
}
