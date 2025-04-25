package com.aml_service.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String reference) {
        super("Transaction " + reference + " not found");
    }
}
