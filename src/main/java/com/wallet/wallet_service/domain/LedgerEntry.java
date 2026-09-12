package com.wallet.wallet_service.domain;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry implements Persistable<UUID> {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(name = "entry_type", nullable = false)
    private String entryType;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    public LedgerEntry() {}

    public LedgerEntry(UUID id, Transaction transaction, Wallet wallet, String entryType, Long amount, String currency) {
        this.id = id;
        this.transaction = transaction;
        this.wallet = wallet;
        this.entryType = entryType;
        this.amount = amount;
        this.currency = currency;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
