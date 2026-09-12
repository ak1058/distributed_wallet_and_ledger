package com.wallet.wallet_service.repository;

import com.wallet.wallet_service.domain.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    
    // Using SKIP LOCKED to allow concurrent publishers if needed
    @Query(value = "SELECT * FROM outbox_events WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT ?1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findPendingEvents(int limit);
}
