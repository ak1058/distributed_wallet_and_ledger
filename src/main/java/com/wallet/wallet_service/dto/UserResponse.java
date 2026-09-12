package com.wallet.wallet_service.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String externalId;
    private String name;
    private String email;
    private String status;
    private ZonedDateTime createdAt;

    public UserResponse(UUID id, String externalId, String name, String email, String status, ZonedDateTime createdAt) {
        this.id = id;
        this.externalId = externalId;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
