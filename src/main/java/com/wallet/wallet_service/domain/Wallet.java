package com.wallet.wallet_service.domain;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "currency"})
})
public class Wallet {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Long balance = 0L;

    @Column(nullable = false)
    private String status;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public Wallet() {}

    public Wallet(UUID id, User user, String currency, String status) {
        this.id = id;
        this.user = user;
        this.currency = currency;
        this.status = status;
        this.balance = 0L;
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

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
