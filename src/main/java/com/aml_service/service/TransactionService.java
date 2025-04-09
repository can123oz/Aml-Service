package com.aml_service.service;

import com.aml_service.model.Transaction;
import com.aml_service.model.TransactionOutbox;
import com.aml_service.repository.OutboxRepository;
import com.aml_service.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

import static com.aml_service.model.Constants.PENDING;

@Component
public class TransactionService {

    final String prefix = "[Service]";
    private final TransactionRepository transactionRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper mapper;

    public TransactionService(TransactionRepository transactionRepository,
                              OutboxRepository outboxRepository,
                              ObjectMapper mapper) {
        this.transactionRepository = transactionRepository;
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
    }

    @Transactional(timeout = 20)
    public void processTransaction(Transaction transaction) throws JsonProcessingException {
        Transaction trxEntity = transactionRepository.saveAndFlush(transaction);
        TransactionOutbox outbox = new TransactionOutbox();
        outbox.setStatus(PENDING);
        outbox.setEvent(mapper.writeValueAsString(trxEntity));
        outbox.setTransactionId(trxEntity.getId());

        outboxRepository.saveAndFlush(outbox);
    }
}
