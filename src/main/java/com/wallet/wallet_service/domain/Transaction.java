package com.wallet.wallet_service.domain;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "transactions")
public class Transaction implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_wallet_id")
    private Wallet sourceWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_wallet_id")
    private Wallet destinationWallet;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public Transaction() {}

    public Transaction(UUID id, String idempotencyKey, Wallet sourceWallet, Wallet destinationWallet, Long amount, String currency, String status) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.sourceWallet = sourceWallet;
        this.destinationWallet = destinationWallet;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public Wallet getSourceWallet() { return sourceWallet; }
    public void setSourceWallet(Wallet sourceWallet) { this.sourceWallet = sourceWallet; }
    public Wallet getDestinationWallet() { return destinationWallet; }
    public void setDestinationWallet(Wallet destinationWallet) { this.destinationWallet = destinationWallet; }
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
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
