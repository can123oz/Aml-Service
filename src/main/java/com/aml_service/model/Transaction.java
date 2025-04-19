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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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

    public Transaction() {
    }

    public Transaction(String id, String type, BigDecimal amount, String currency, String status) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
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
