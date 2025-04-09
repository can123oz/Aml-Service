package com.aml_service.controller;

import com.aml_service.client.KafkaProducer;
import com.aml_service.model.Transaction;
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

    @PostMapping("/publish-test")
    public ResponseEntity<?> publishTest(@RequestBody Transaction transaction) {
        messageBus.publish(transaction);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/consume-test")
    public ResponseEntity<?> consumeTest(@RequestBody Transaction transaction) {
        messageBus.publishTest("aml-inbound", transaction);
        return ResponseEntity.ok().build();
    }
}
