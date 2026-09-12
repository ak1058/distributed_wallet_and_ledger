package com.wallet.wallet_service.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import java.io.Serializable;

public class WalletResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID userId;
    private String currency;
    private Long balance;
    private String status;
    private ZonedDateTime createdAt;

    public WalletResponse() {}

    public WalletResponse(UUID id, UUID userId, String currency, Long balance, String status, ZonedDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
