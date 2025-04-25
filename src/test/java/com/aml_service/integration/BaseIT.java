package com.aml_service.integration;

import com.aml_service.model.TransactionEntity;
import com.aml_service.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BaseIT extends AbstractIntegrationTest {

    @Autowired
    private TransactionRepository repository;

    @Test
    public void postgresReadyTest() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    public void postgresShouldSave() {
        // Given
        TransactionEntity entity = new TransactionEntity();
        entity.setAmlResult("SUCCESS");
        entity.setStatus("COMPLETED");
        entity.setType("OUTBOUND-TEST");
        entity.setAmount(BigDecimal.TEN);
        entity.setCurrency("EUR");

        // When
        TransactionEntity savedEntity = repository.saveAndFlush(entity);

        // Then
        assertNotNull(savedEntity);
        assertEquals(savedEntity.getAmlResult(), entity.getAmlResult());
    }

//    @Test
//    public void kafkaReadyTest() {
//        assertTrue(kafka.isCreated());
//        assertTrue(kafka.isRunning());
//    }

    @AfterEach
    public void cleanup() {
        repository.deleteAll();
    }
}
