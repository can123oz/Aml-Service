package com.aml_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "reference")
    private String reference;

    @Column(name = "type")
    private String type;

    @Column(name = "amount")
    @Min(value = 0)
    private BigDecimal amount;

    @Column(name = "currency")
    @NotNull
    private String currency;

    @Column(name = "status")
    private String status;

    @Column(name = "aml_result")
    private String amlResult;

    public TransactionEntity() {
    }

    public TransactionEntity(String type, String reference, BigDecimal amount,
                             String currency, String status, String amlResult) {
        this.type = type;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.amlResult = amlResult;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
