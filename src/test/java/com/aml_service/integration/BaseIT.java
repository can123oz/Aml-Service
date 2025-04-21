package com.aml_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BaseIT extends AbstractIntegrationTest {

    @Test
    public void postgresReadyTest() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    public void kafkaReadyTest() {
        assertTrue(kafka.isCreated());
        assertTrue(kafka.isRunning());
    }
}
