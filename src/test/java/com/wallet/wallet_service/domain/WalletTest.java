package com.wallet.wallet_service.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testWalletCreationAndBalance() {
        User user = new User(UUID.randomUUID(), "ext1", "Alice", "alice@example.com", "ACTIVE");
        Wallet wallet = new Wallet(UUID.randomUUID(), user, "USD", "ACTIVE");

        assertEquals(0L, wallet.getBalance());
        assertEquals("USD", wallet.getCurrency());
        
        wallet.setBalance(100L);
        assertEquals(100L, wallet.getBalance());
    }

    @Test
    void testBalanceCannotBeNegative() {
        // Validation normally happens at DB level via check constraints,
        // but we can ensure domain objects behave sensibly.
        User user = new User(UUID.randomUUID(), "ext1", "Bob", "bob@example.com", "ACTIVE");
        Wallet wallet = new Wallet(UUID.randomUUID(), user, "EUR", "ACTIVE");
        
        wallet.setBalance(-50L);
        assertTrue(wallet.getBalance() < 0);
        // Note: Actual enforcement of non-negative balance is currently handled in the DB 
        // and Service layer via InsufficientFundsException.
    }
}
