package com.wallet.wallet_service.domain;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "users")
public class User implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public User() {}

    public User(UUID id, String externalId, String name, String email, String status) {
        this.id = id;
        this.externalId = externalId;
        this.name = name;
        this.email = email;
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

    // Getters and Setters
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
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
