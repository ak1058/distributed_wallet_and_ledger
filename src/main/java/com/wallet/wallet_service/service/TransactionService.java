package com.wallet.wallet_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.wallet_service.domain.*;
import com.wallet.wallet_service.dto.*;
import com.wallet.wallet_service.exception.InsufficientFundsException;
import com.wallet.wallet_service.exception.ResourceNotFoundException;
import com.wallet.wallet_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ObjectMapper objectMapper;

    public TransactionService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            LedgerEntryRepository ledgerEntryRepository,
            OutboxEventRepository outboxEventRepository,
            IdempotencyRecordRepository idempotencyRecordRepository,
            ObjectMapper objectMapper) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 50))
    @org.springframework.cache.annotation.CacheEvict(value = "wallet-balances", key = "#request.sourceWalletId")
    public TransactionResponse transfer(String idempotencyKey, TransferRequest request) {
        // 1. Check idempotency
        Optional<IdempotencyRecord> existingRecord = idempotencyRecordRepository.findById(idempotencyKey);
        if (existingRecord.isPresent()) {
            IdempotencyRecord record = existingRecord.get();
            try {
                return objectMapper.readValue(record.getResponseBody(), TransactionResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize idempotency response", e);
            }
        }

        // 2. Fetch wallets
        Wallet sourceWallet = walletRepository.findById(request.getSourceWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Source wallet not found"));
        Wallet destinationWallet = walletRepository.findById(request.getDestinationWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));

        if (!sourceWallet.getCurrency().equals(request.getCurrency()) || !destinationWallet.getCurrency().equals(request.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        if (sourceWallet.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Insufficient funds in source wallet");
        }

        // 3. Update balances (Optimistic locking is enforced by @Version on Wallet)
        sourceWallet.setBalance(sourceWallet.getBalance() - request.getAmount());
        destinationWallet.setBalance(destinationWallet.getBalance() + request.getAmount());

        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        // 4. Create Transaction
        Transaction tx = new Transaction(
                UUID.randomUUID(),
                idempotencyKey,
                sourceWallet,
                destinationWallet,
                request.getAmount(),
                request.getCurrency(),
                "COMPLETED"
        );
        transactionRepository.save(tx);

        // 5. Create Ledger Entries
        LedgerEntry debit = new LedgerEntry(UUID.randomUUID(), tx, sourceWallet, "DEBIT", request.getAmount(), request.getCurrency());
        LedgerEntry credit = new LedgerEntry(UUID.randomUUID(), tx, destinationWallet, "CREDIT", request.getAmount(), request.getCurrency());
        ledgerEntryRepository.save(debit);
        ledgerEntryRepository.save(credit);

        // 6. Create Outbox Event
        TransactionResponse response = mapToResponse(tx);
        createOutboxEvent(tx.getId().toString(), "TRANSACTION", "TRANSFER_COMPLETED", response);

        // 7. Save Idempotency Record
        saveIdempotencyRecord(idempotencyKey, "hash-placeholder", tx.getId(), 200, response);

        return response;
    }

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 50))
    @org.springframework.cache.annotation.CacheEvict(value = "wallet-balances", key = "#walletId")
    public TransactionResponse deposit(String idempotencyKey, UUID walletId, DepositRequest request) {
        // Idempotency check
        Optional<IdempotencyRecord> existingRecord = idempotencyRecordRepository.findById(idempotencyKey);
        if (existingRecord.isPresent()) {
            try {
                return objectMapper.readValue(existingRecord.get().getResponseBody(), TransactionResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize idempotency response", e);
            }
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (!wallet.getCurrency().equals(request.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        wallet.setBalance(wallet.getBalance() + request.getAmount());
        walletRepository.save(wallet);

        Transaction tx = new Transaction(
                UUID.randomUUID(),
                idempotencyKey,
                null,
                wallet,
                request.getAmount(),
                request.getCurrency(),
                "COMPLETED"
        );
        transactionRepository.save(tx);

        LedgerEntry credit = new LedgerEntry(UUID.randomUUID(), tx, wallet, "CREDIT", request.getAmount(), request.getCurrency());
        ledgerEntryRepository.save(credit);

        TransactionResponse response = mapToResponse(tx);
        createOutboxEvent(tx.getId().toString(), "TRANSACTION", "DEPOSIT_COMPLETED", response);
        saveIdempotencyRecord(idempotencyKey, "hash-placeholder", tx.getId(), 200, response);

        return response;
    }

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 5, backoff = @Backoff(delay = 50))
    @org.springframework.cache.annotation.CacheEvict(value = "wallet-balances", key = "#walletId")
    public TransactionResponse withdraw(String idempotencyKey, UUID walletId, WithdrawRequest request) {
        // Idempotency check
        Optional<IdempotencyRecord> existingRecord = idempotencyRecordRepository.findById(idempotencyKey);
        if (existingRecord.isPresent()) {
            try {
                return objectMapper.readValue(existingRecord.get().getResponseBody(), TransactionResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize idempotency response", e);
            }
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (!wallet.getCurrency().equals(request.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        if (wallet.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance() - request.getAmount());
        walletRepository.save(wallet);

        Transaction tx = new Transaction(
                UUID.randomUUID(),
                idempotencyKey,
                wallet,
                null,
                request.getAmount(),
                request.getCurrency(),
                "COMPLETED"
        );
        transactionRepository.save(tx);

        LedgerEntry debit = new LedgerEntry(UUID.randomUUID(), tx, wallet, "DEBIT", request.getAmount(), request.getCurrency());
        ledgerEntryRepository.save(debit);

        TransactionResponse response = mapToResponse(tx);
        createOutboxEvent(tx.getId().toString(), "TRANSACTION", "WITHDRAW_COMPLETED", response);
        saveIdempotencyRecord(idempotencyKey, "hash-placeholder", tx.getId(), 200, response);

        return response;
    }

    private void createOutboxEvent(String aggregateId, String aggregateType, String eventType, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            OutboxEvent outboxEvent = new OutboxEvent(
                    UUID.randomUUID(),
                    aggregateId,
                    aggregateType,
                    eventType,
                    payloadJson,
                    "PENDING"
            );
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox payload", e);
        }
    }

    private void saveIdempotencyRecord(String key, String hash, UUID txId, int status, Object responseBody) {
        try {
            String responseJson = objectMapper.writeValueAsString(responseBody);
            IdempotencyRecord record = new IdempotencyRecord(key, hash, txId, status, responseJson);
            idempotencyRecordRepository.save(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize idempotency response", e);
        }
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getIdempotencyKey(),
                tx.getSourceWallet() != null ? tx.getSourceWallet().getId() : null,
                tx.getDestinationWallet() != null ? tx.getDestinationWallet().getId() : null,
                tx.getAmount(),
                tx.getCurrency(),
                tx.getStatus(),
                tx.getFailureReason(),
                tx.getCreatedAt()
        );
    }
}
