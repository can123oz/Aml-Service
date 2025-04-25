package com.aml_service.contract;

import com.aml_service.integration.AbstractIntegrationTest;
import com.aml_service.model.TransactionEntity;
import com.aml_service.model.TransactionStates;
import com.aml_service.repository.TransactionRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

@AutoConfigureMockMvc
@SpringBootTest
public class ContractVerifierBase extends AbstractIntegrationTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private TransactionRepository repository;

    protected TransactionEntity entity;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        entity = repository.saveAndFlush(
                new TransactionEntity("OUTBOUND",
                        "test-reference",
                        BigDecimal.TEN,
                        "EUR",
                        "TEST",
                        TransactionStates.PASS.name()));
    }

    @AfterEach
    public void cleanup() {
        repository.deleteById(entity.getId());
    }
}