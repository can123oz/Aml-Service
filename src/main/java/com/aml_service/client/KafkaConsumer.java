package com.aml_service.client;

import com.aml_service.exception.ValidationException;
import com.aml_service.model.Transaction;
import com.aml_service.model.TransactionType;
import com.aml_service.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class KafkaConsumer {

    final String prefix = "[Consumer]";
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper mapper;
    private final Validator validator;
    private final TransactionService service;

    public KafkaConsumer(ObjectMapper mapper,
                         Validator validator,
                         TransactionService service) {
        this.mapper = mapper;
        this.validator = validator;
        this.service = service;
    }

    @RetryableTopic(attempts = "4",
            kafkaTemplate = "retryableTopicKafkaTemplate",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = {ValidationException.class})
    @KafkaListener(topics = "${kafka.inbound-topic}",
            groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Valid Transaction trx, Acknowledgment acknowledgment, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            validateMessage(trx);

            logger.info("{} Message received the process will start: {}", prefix, trx);

            if (!TransactionType.OUTBOUND.name().equalsIgnoreCase(trx.getType())) {
                // just to simulate dead letter queue
                throw new RuntimeException();
                // return;
            }

            service.processTransaction(trx);
            acknowledgment.acknowledge();
            logger.info("{} Logic passed, acknowledgment given for message: {}", prefix, trx);
        } catch (ValidationException validationException) {
            logger.error("{} Validation failed, no acknowledgment given for message: {}", prefix, trx);
            throw validationException;
        } catch (Exception e) {
            logger.error("{} Error, no acknowledgment given for message: {}", prefix, trx);
            throw new RuntimeException(e);
        }
    }

    @DltHandler
    public void handleDltTransaction(Transaction transaction,
                                     Acknowledgment acknowledgment,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        logger.info("Event on dlt topic={}, transaction={}", topic, transaction);
        acknowledgment.acknowledge();
    }

    private void validateMessage(Transaction trx) {
        Set<ConstraintViolation<Transaction>> violations = validator.validate(trx);

        if (!violations.isEmpty()) {
            Map<String, String> errorMessages = new HashMap<>();
            violations.forEach(err -> {
                String key = err.getPropertyPath().toString();
                if (errorMessages.containsKey(key)) {
                    errorMessages.put(key, errorMessages.get(key) + " and " + err.getMessage());
                } else {
                    errorMessages.put(key, err.getMessage());
                }
            });
            logger.error("Validation failed for message: {}", errorMessages);
            throw new ValidationException(errorMessages.toString());
        }
    }
}
