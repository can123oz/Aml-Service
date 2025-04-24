# 🛡️ AML Service

This microservice is part of a larger system responsible for anti-money laundering (AML) transaction processing. It leverages robust messaging and data integrity mechanisms to ensure reliable and traceable communication through Apache Kafka.

#### This service is ready to scale horizontally, allowing multiple instances of the service to run concurrently while safely coordinating through database-level locking and Kafka partitioning.

---

## 🧱 Tech Stack

- **Java 17**
- **Spring Boot**
- **Gradle**
- **PostgreSQL**
- **Apache Kafka**
- **Transactional Outbox Pattern**
- **Kafka Retryable Topics**
- **Dead Letter Topics (DLT)**

---

## 🎯 Overview

This service handles two main entities:

- `Transaction`: Represents a financial transaction that needs to be validated and processed.
- `TransactionEntity`: Represents a financial transaction that needs to be saved.
- `TransactionOutbox`: Acts as a reliable queue within the database to ensure that all events are eventually published to Kafka, even in the presence of failures.

---

## 📦 Design Patterns Used

### ✅ Transactional Outbox Pattern

To ensure that database updates and Kafka message publishing are done reliably and consistently, we implement the **Transactional Outbox Pattern**. This decouples database transactions from Kafka delivery and helps ensure at-least-once delivery guarantees without introducing dual-write inconsistencies.

1. `TransactionEntity` is saved and an associated `TransactionOutboxEntity` entry is created in a single transaction.
2. A scheduled processor (`OutboxProcessor`) periodically polls `TransactionOutboxEntity` for pending entries and publishes them to Kafka.
3. Once published, the outbox status is updated (or marked as failed if Kafka publishing fails).
4. Old processed outbox entries are deleted after a retention period to keep the database lean.

### 🔁 Kafka Retry & DLT Handling

- Messages are consumed from a Kafka topic using `@KafkaListener`.
- A custom validator is used to validate inbound transactions.
- If processing fails (e.g., exceptions), messages are retried up to a specified number of attempts using **Retryable Topics**.
- Messages that still fail after retries are routed to a **Dead Letter Topic (DLT)**.
- DLT messages are acknowledged and logged for manual inspection or automated remediation.

---

## ⚙️ Key Components

### 🧩 `OutboxProcessor`

- Polls `TransactionOutboxEntity` for messages with `PENDING` status.
- Attempts to publish them to Kafka.
- Updates their status to `PROCESSED` or `FAILED` accordingly.
- The outbox processor is configured with a fixed delay of 20 seconds, meaning it starts a new run 20 seconds after the previous execution finishes.
- Deletes processed entries older than a configurable retention period (default: days).
- When multiple instances of the service are running, a skip-lock mechanism ensures that only one instance processes a given set of outbox entries at a time.


### 🧩 `KafkaConsumer`

- Listens to inbound Kafka messages.
- Validates each `Transaction` object.
- If valid, processes the transaction (only if it's of type `OUTBOUND`).
- Retries failed messages using Kafka Retryable Topics.
- Unrecoverable messages are routed to a Dead Letter Topic (DLT).