package com.aml_service.controller;

import com.aml_service.client.KafkaProducer;
import com.aml_service.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final KafkaProducer messageBus;

    public TestController(KafkaProducer messageBus) {
        this.messageBus = messageBus;
    }

    @Operation(summary = "For Local Testing Only", description = "Triggers the kafka consumer.")
    @PostMapping("/consume-test")
    public ResponseEntity<?> consumeTest(@RequestBody Transaction transaction) {
        messageBus.publishTest("aml-inbound", transaction);
        return ResponseEntity.ok().build();
    }
}
