package com.wallet.wallet_service.reconciliation;

import com.wallet.wallet_service.domain.LedgerEntry;
import com.wallet.wallet_service.domain.Wallet;
import com.wallet.wallet_service.repository.LedgerEntryRepository;
import com.wallet.wallet_service.repository.WalletRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@EnableScheduling
public class ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);
    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    
    private final Counter reconciliationSuccessCounter;
    private final Counter reconciliationMismatchCounter;

    public ReconciliationService(WalletRepository walletRepository, LedgerEntryRepository ledgerEntryRepository, MeterRegistry meterRegistry) {
        this.walletRepository = walletRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        
        this.reconciliationSuccessCounter = Counter.builder("wallet.reconciliation.success")
                .description("Number of successfully reconciled wallets")
                .register(meterRegistry);
        this.reconciliationMismatchCounter = Counter.builder("wallet.reconciliation.mismatch")
                .description("Number of wallets with balance mismatches")
                .register(meterRegistry);
    }

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional(readOnly = true)
    public void reconcileAllWallets() {
        log.info("Starting scheduled reconciliation process");
        List<Wallet> allWallets = walletRepository.findAll();
        
        for (Wallet wallet : allWallets) {
            reconcileWallet(wallet.getId());
        }
        
        log.info("Completed scheduled reconciliation process");
    }

    public void reconcileWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow();
        List<LedgerEntry> entries = ledgerEntryRepository.findByWalletId(walletId);
        
        long sumCredits = 0;
        long sumDebits = 0;
        
        for (LedgerEntry entry : entries) {
            if ("CREDIT".equals(entry.getEntryType())) {
                sumCredits += entry.getAmount();
            } else if ("DEBIT".equals(entry.getEntryType())) {
                sumDebits += entry.getAmount();
            }
        }
        
        long ledgerBalance = sumCredits - sumDebits;
        
        if (ledgerBalance != wallet.getBalance()) {
            log.error("MISMATCH DETECTED: WalletId={}, DBBalance={}, LedgerBalance={}", 
                    walletId, wallet.getBalance(), ledgerBalance);
            reconciliationMismatchCounter.increment();
            
            // In a full implementation we would save to ReconciliationResult repository here
            
        } else {
            log.debug("Wallet reconciled successfully: WalletId={}", walletId);
            reconciliationSuccessCounter.increment();
        }
    }
}
