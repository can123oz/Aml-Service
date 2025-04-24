package com.aml_service.service;

import com.aml_service.client.KafkaProducer;
import com.aml_service.model.TransactionOutboxEntity;
import com.aml_service.model.TransactionStates;
import com.aml_service.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class OutboxProcessor {

    @Value("${outbox.batch-size}")
    private int OUTBOX_BATCH_SIZE;

    @Value("${outbox.retention-days}")
    private int outboxRetentionDays;

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
        List<TransactionOutboxEntity> batch = outboxRepository.findAndLockBatch(TransactionStates.PENDING.name(), OUTBOX_BATCH_SIZE);
        logger.info("{} Processing outbox size: {}", prefix, batch.size());

        if (batch.isEmpty()) {
            return;
        }

        for (TransactionOutboxEntity outbox : batch) {
            try {
                producer.publish(outbox.getEvent());
                outbox.processTransaction();
            } catch (Exception e) {
                logger.error("{} Failed to publish event {}, exception: {}",
                        prefix, outbox.getId(), e.toString());
                outbox.setStatus(TransactionStates.FAILED.name());
            }
        }

        outboxRepository.saveAllAndFlush(batch);
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void deleteOldProcessedOutbox() {
        int deleted = outboxRepository.deleteOldProcessed(Instant.now().minus(Duration.ofDays(outboxRetentionDays)));
        logger.info("{} Deleted {} old processed outbox rows", prefix, deleted);
    }
}
