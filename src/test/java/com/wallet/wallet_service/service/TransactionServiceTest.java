package com.wallet.wallet_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.wallet_service.domain.*;
import com.wallet.wallet_service.dto.*;
import com.wallet.wallet_service.exception.InsufficientFundsException;
import com.wallet.wallet_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private LedgerEntryRepository ledgerEntryRepository;
    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private IdempotencyRecordRepository idempotencyRecordRepository;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.findAndRegisterModules();
        transactionService = new TransactionService(
                walletRepository, transactionRepository, ledgerEntryRepository,
                outboxEventRepository, idempotencyRecordRepository, objectMapper);
    }

    @Test
    void testTransferSuccess() {
        UUID sourceId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        
        User user = new User();
        Wallet source = new Wallet(sourceId, user, "USD", "ACTIVE");
        source.setBalance(100L);
        Wallet dest = new Wallet(destId, user, "USD", "ACTIVE");
        dest.setBalance(50L);

        when(walletRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(walletRepository.findById(destId)).thenReturn(Optional.of(dest));
        when(idempotencyRecordRepository.findById(anyString())).thenReturn(Optional.empty());

        TransferRequest req = new TransferRequest();
        req.setSourceWalletId(sourceId);
        req.setDestinationWalletId(destId);
        req.setAmount(50L);
        req.setCurrency("USD");

        TransactionResponse res = transactionService.transfer("idem-key-1", req);

        assertNotNull(res);
        assertEquals(50L, source.getBalance());
        assertEquals(100L, dest.getBalance());
        
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(ledgerEntryRepository, times(2)).save(any(LedgerEntry.class));
        verify(outboxEventRepository, times(1)).save(any(OutboxEvent.class));
        verify(idempotencyRecordRepository, times(1)).save(any(IdempotencyRecord.class));
    }

    @Test
    void testTransferInsufficientFunds() {
        UUID sourceId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        
        User user = new User();
        Wallet source = new Wallet(sourceId, user, "USD", "ACTIVE");
        source.setBalance(30L);
        Wallet dest = new Wallet(destId, user, "USD", "ACTIVE");
        dest.setBalance(50L);

        when(walletRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(walletRepository.findById(destId)).thenReturn(Optional.of(dest));

        TransferRequest req = new TransferRequest();
        req.setSourceWalletId(sourceId);
        req.setDestinationWalletId(destId);
        req.setAmount(50L);
        req.setCurrency("USD");

        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer("idem-key-2", req));
        
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testTransferIdempotency() throws Exception {
        String key = "idem-key-3";
        TransactionResponse mockResponse = new TransactionResponse(UUID.randomUUID(), key, null, null, 100L, "USD", "COMPLETED", null, null);
        String mockResponseStr = objectMapper.writeValueAsString(mockResponse);
        
        IdempotencyRecord record = new IdempotencyRecord();
        record.setResponseBody(mockResponseStr);
        
        when(idempotencyRecordRepository.findById(key)).thenReturn(Optional.of(record));

        TransferRequest req = new TransferRequest();
        TransactionResponse res = transactionService.transfer(key, req);
        
        assertNotNull(res);
        assertEquals("COMPLETED", res.getStatus());
        verify(walletRepository, never()).findById(any(UUID.class));
    }
}
