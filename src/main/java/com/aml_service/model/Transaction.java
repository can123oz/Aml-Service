package com.aml_service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record Transaction(String id,
                          String type,
                          @Min(value = 0) BigDecimal amount,
                          @NotNull String currency,
                          String status) {
    public TransactionEntity toEntity(TransactionStates amlResult) {
        return new TransactionEntity(type, amount, currency, status, amlResult.name());
    }
}