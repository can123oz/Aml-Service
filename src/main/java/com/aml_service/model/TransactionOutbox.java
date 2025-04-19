package com.aml_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static com.aml_service.model.Constants.PENDING;
import static com.aml_service.model.Constants.PROCESSED;

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

    public TransactionOutbox(String transactionId, String event) {
        this.transactionId = transactionId;
        this.event = event;
        this.status = PENDING;
    }

    public TransactionOutbox() {}

    public void processTransaction() {
        this.status = PROCESSED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
