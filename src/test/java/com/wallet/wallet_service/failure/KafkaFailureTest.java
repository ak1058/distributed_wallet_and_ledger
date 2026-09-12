package com.wallet.wallet_service.failure;

import com.wallet.wallet_service.kafka.NotificationConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public class KafkaFailureTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private NotificationConsumer notificationConsumer;

    @Test
    void testConsumerIdempotencyOnRedelivery() {
        // We bypass the actual kafka template for a direct unit-like test of the component's idempotency logic
        String payload = "{\"eventId\": \"abc-123\", \"amount\": 100}";

        // First delivery
        notificationConsumer.consumeEvent(payload);

        // Simulated redelivery (due to crash before ack)
        // Expected: it should log "already processed" and return without throwing exceptions or double processing.
        notificationConsumer.consumeEvent(payload);
    }
}
