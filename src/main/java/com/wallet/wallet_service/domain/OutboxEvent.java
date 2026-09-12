package com.wallet.wallet_service.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "published_at")
    private ZonedDateTime publishedAt;

    public OutboxEvent() {}

    public OutboxEvent(UUID id, String aggregateId, String aggregateType, String eventType, String payload, String status) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.retryCount = 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(ZonedDateTime publishedAt) { this.publishedAt = publishedAt; }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
