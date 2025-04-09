package com.aml_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "transaction_outbox")
@Getter
@Setter
public class TransactionOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "status")
    private String status;

    @Column(name = "event")
    private String event;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public TransactionOutbox() {
    }

    public TransactionOutbox(String id, String transactionId, String status, String event) {
        this.id = id;
        this.transactionId = transactionId;
        this.status = status;
        this.event = event;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
