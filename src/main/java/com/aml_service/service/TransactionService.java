package com.aml_service.service;

import com.aml_service.model.Transaction;
import com.aml_service.model.TransactionOutbox;
import com.aml_service.repository.OutboxRepository;
import com.aml_service.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

@Component
public class TransactionService {

    final String prefix = "[Service]";
    private final TransactionRepository transactionRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepository,
                              OutboxRepository outboxRepository,
                              ObjectMapper mapper) {
        this.transactionRepository = transactionRepository;
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Transactional(timeout = 20)
    public void processTransaction(Transaction transaction) {
        try {
            // Simulate transaction processing with a provider or internal calculation.
            Thread.sleep(2000);

            Transaction trxEntity = transactionRepository.saveAndFlush(transaction);
            TransactionOutbox outbox = new TransactionOutbox(trxEntity.getId(), mapper.writeValueAsString(trxEntity));

            outboxRepository.saveAndFlush(outbox);

            logger.info("{} Processed transaction {}", prefix, trxEntity.getId());
        } catch (JsonProcessingException e) {
            logger.error("{} Failed to serialize transaction {}", prefix, transaction.getId());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error("{} Failed to process transaction {}", prefix, transaction.getId());
            throw new RuntimeException(e);
        }
    }
}