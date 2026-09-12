package com.wallet.wallet_service.repository;

import com.wallet.wallet_service.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> findByWalletId(UUID walletId);
    List<LedgerEntry> findByTransactionId(UUID transactionId);
}
