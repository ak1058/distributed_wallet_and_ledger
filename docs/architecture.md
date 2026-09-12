# Architecture Overview

## High-Level Flow

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant DB
    participant OutboxPublisher
    participant Kafka

    Client->>API: POST /transfers (Idempotency-Key)
    API->>DB: Check Idempotency Record
    API->>DB: Deduct Source, Add Dest Balance
    API->>DB: Create Transaction & Ledger Entries
    API->>DB: Create Outbox Event
    API->>DB: Save Idempotency Record
    DB-->>API: COMMIT
    API-->>Client: 201 Created

    OutboxPublisher->>DB: SELECT PENDING FOR UPDATE SKIP LOCKED
    OutboxPublisher->>Kafka: Publish Event
    OutboxPublisher->>DB: UPDATE status='PUBLISHED'
```

## Double-Entry Ledger
The system maintains the invariant `SUM(CREDITS) - SUM(DEBITS) = 0`. No historical ledger entry is ever updated.

## Concurrency
We use Optimistic Locking on the `Wallet` entity (via `@Version`) and DB constraints to protect against double spending.
