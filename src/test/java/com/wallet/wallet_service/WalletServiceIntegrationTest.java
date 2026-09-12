package com.wallet.wallet_service;

import com.wallet.wallet_service.domain.User;
import com.wallet.wallet_service.domain.Wallet;
import com.wallet.wallet_service.dto.DepositRequest;
import com.wallet.wallet_service.dto.TransactionResponse;
import com.wallet.wallet_service.dto.TransferRequest;
import com.wallet.wallet_service.repository.UserRepository;
import com.wallet.wallet_service.repository.WalletRepository;
import com.wallet.wallet_service.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
public class WalletServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private Wallet sourceWallet;
    private Wallet destWallet;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        userRepository.deleteAll();

        User user1 = new User(UUID.randomUUID(), "ext1", "Alice", "alice@example.com", "ACTIVE");
        User user2 = new User(UUID.randomUUID(), "ext2", "Bob", "bob@example.com", "ACTIVE");
        userRepository.save(user1);
        userRepository.save(user2);

        sourceWallet = new Wallet(UUID.randomUUID(), user1, "INR", "ACTIVE");
        destWallet = new Wallet(UUID.randomUUID(), user2, "INR", "ACTIVE");
        walletRepository.save(sourceWallet);
        walletRepository.save(destWallet);
    }

    @Test
    void testDepositAndTransfer() {
        // Deposit
        DepositRequest depositReq = new DepositRequest();
        depositReq.setAmount(1000L);
        depositReq.setCurrency("INR");
        
        TransactionResponse depositRes = transactionService.deposit(UUID.randomUUID().toString(), sourceWallet.getId(), depositReq);
        assertNotNull(depositRes.getId());

        // Transfer
        TransferRequest transferReq = new TransferRequest();
        transferReq.setSourceWalletId(sourceWallet.getId());
        transferReq.setDestinationWalletId(destWallet.getId());
        transferReq.setAmount(500L);
        transferReq.setCurrency("INR");
        
        TransactionResponse transferRes = transactionService.transfer(UUID.randomUUID().toString(), transferReq);
        assertEquals("COMPLETED", transferRes.getStatus());

        // Assert Balances
        Wallet sWallet = walletRepository.findById(sourceWallet.getId()).get();
        Wallet dWallet = walletRepository.findById(destWallet.getId()).get();
        assertEquals(500L, sWallet.getBalance());
        assertEquals(500L, dWallet.getBalance());
    }
}
