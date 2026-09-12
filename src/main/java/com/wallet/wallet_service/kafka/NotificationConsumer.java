package com.wallet.wallet_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    // Simple in-memory idempotency check for demonstration (In production, use Redis or DB)
    private final Set<String> processedEvents = new HashSet<>();

    @RetryableTopic(
            attempts = "3",
            autoCreateTopics = "false",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "wallet.events", groupId = "wallet-notification-group")
    public void consumeEvent(String payload) {
        log.info("Received event for notification: {}", payload);

        // Simulated idempotency check using hash of payload (in reality use eventId)
        String eventId = String.valueOf(payload.hashCode());
        
        synchronized (processedEvents) {
            if (processedEvents.contains(eventId)) {
                log.info("Event {} already processed. Skipping.", eventId);
                return;
            }
            processedEvents.add(eventId);
        }

        // Simulate failure for DLQ demonstration if payload contains "FAIL_ME"
        if (payload.contains("FAIL_ME")) {
            throw new RuntimeException("Simulated transient failure for DLQ routing");
        }

        log.info("Notification sent for event {}", eventId);
    }
}
