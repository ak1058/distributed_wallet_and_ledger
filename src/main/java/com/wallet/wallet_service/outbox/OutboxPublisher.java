package com.wallet.wallet_service.outbox;

import com.wallet.wallet_service.domain.OutboxEvent;
import com.wallet.wallet_service.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@EnableScheduling
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findPendingEvents(100);
        
        for (OutboxEvent event : pendingEvents) {
            try {
                // Topic naming convention based on aggregate type
                String topic = "wallet.events";
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload()).get();
                
                event.setStatus("PUBLISHED");
                event.setPublishedAt(ZonedDateTime.now());
                outboxEventRepository.save(event);
                
                log.info("Published outbox event {}", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish event {}", event.getId(), e);
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= 5) {
                    event.setStatus("FAILED"); // DLQ handling logic will kick in or admin intervention
                }
                outboxEventRepository.save(event);
            }
        }
    }
}
