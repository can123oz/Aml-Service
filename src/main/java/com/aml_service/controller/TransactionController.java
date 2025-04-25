package com.aml_service.controller;

import com.aml_service.model.TransactionResponse;
import com.aml_service.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "For Aml Result Fetching Manually Only", description = "Dashboards can use it.")
    public ResponseEntity<TransactionResponse> getTransaction(String id) {
        return ResponseEntity.ok(service.getTransaction(id));
    }
}
