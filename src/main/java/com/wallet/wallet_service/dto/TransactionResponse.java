package com.wallet.wallet_service.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class TransactionResponse {
    private UUID id;
    private String idempotencyKey;
    private UUID sourceWalletId;
    private UUID destinationWalletId;
    private Long amount;
    private String currency;
    private String status;
    private String failureReason;
    private ZonedDateTime createdAt;

    public TransactionResponse() {}

    public TransactionResponse(UUID id, String idempotencyKey, UUID sourceWalletId, UUID destinationWalletId, Long amount, String currency, String status, String failureReason, ZonedDateTime createdAt) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public UUID getSourceWalletId() { return sourceWalletId; }
    public void setSourceWalletId(UUID sourceWalletId) { this.sourceWalletId = sourceWalletId; }
    public UUID getDestinationWalletId() { return destinationWalletId; }
    public void setDestinationWalletId(UUID destinationWalletId) { this.destinationWalletId = destinationWalletId; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
