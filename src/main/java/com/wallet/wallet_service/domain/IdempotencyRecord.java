package com.wallet.wallet_service.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "idempotency_records")
public class IdempotencyRecord implements Persistable<String> {

    @Id
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false)
    private String requestHash;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "response_status", nullable = false)
    private Integer responseStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_body", columnDefinition = "jsonb")
    private String responseBody;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    public IdempotencyRecord() {}

    public IdempotencyRecord(String idempotencyKey, String requestHash, UUID transactionId, Integer responseStatus, String responseBody) {
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.transactionId = transactionId;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getRequestHash() { return requestHash; }
    public void setRequestHash(String requestHash) { this.requestHash = requestHash; }
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public Integer getResponseStatus() { return responseStatus; }
    public void setResponseStatus(Integer responseStatus) { this.responseStatus = responseStatus; }
    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }

    @Override
    public String getId() {
        return this.idempotencyKey;
    }
}
