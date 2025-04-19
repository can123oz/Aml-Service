package com.aml_service.service;

import com.aml_service.client.KafkaProducer;
import com.aml_service.model.TransactionOutbox;
import com.aml_service.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.aml_service.model.Constants.*;

@Component
public class OutboxProcessor {

    final String prefix = "[OutboxProcessor]";
    private final OutboxRepository outboxRepository;
    private final KafkaProducer producer;
    private final Logger logger = LoggerFactory.getLogger(OutboxProcessor.class);

    public OutboxProcessor(OutboxRepository outboxRepository,
                           KafkaProducer producer) {
        this.outboxRepository = outboxRepository;
        this.producer = producer;
    }

    @Transactional(timeout = 600)
    @Scheduled(fixedDelay = 20000)
    public void processOutbox() {
        List<TransactionOutbox> batch = outboxRepository.findAndLockBatch(PENDING, 30);
        logger.info("{} Processing outbox size: {}", prefix, batch.size());

        if (batch.isEmpty()) {
            return;
        }

        for (TransactionOutbox outbox : batch) {
            try {
                producer.publish(outbox.getEvent());
                outbox.processTransaction();
            } catch (Exception e) {
                logger.error("{} Failed to publish event {}, exception: {}",
                        prefix, outbox.getId(), e.toString());
                outbox.setStatus(FAILED);
            }
        }

        outboxRepository.saveAllAndFlush(batch);
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void deleteOldProcessedOutbox() {
        int deleted = outboxRepository.deleteOldProcessed(Instant.now().minus(Duration.ofDays(3)));
        logger.info("Deleted {} old processed outbox rows", deleted);
    }
}
