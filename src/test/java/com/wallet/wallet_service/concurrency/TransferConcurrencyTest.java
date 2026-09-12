package com.wallet.wallet_service.concurrency;

import com.wallet.wallet_service.domain.User;
import com.wallet.wallet_service.domain.Wallet;
import com.wallet.wallet_service.dto.TransferRequest;
import com.wallet.wallet_service.repository.UserRepository;
import com.wallet.wallet_service.repository.WalletRepository;
import com.wallet.wallet_service.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class TransferConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(TransferConcurrencyTest.class);

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

        User user1 = new User(UUID.randomUUID(), "ext1-c", "Alice", "alice.c@example.com", "ACTIVE");
        User user2 = new User(UUID.randomUUID(), "ext2-c", "Bob", "bob.c@example.com", "ACTIVE");
        userRepository.save(user1);
        userRepository.save(user2);

        sourceWallet = new Wallet(UUID.randomUUID(), user1, "INR", "ACTIVE");
        sourceWallet.setBalance(1000L); // 1000 INR
        destWallet = new Wallet(UUID.randomUUID(), user2, "INR", "ACTIVE");
        destWallet.setBalance(0L);
        walletRepository.save(sourceWallet);
        walletRepository.save(destWallet);
    }

    @Test
    void testConcurrentTransfersAvoidDoubleSpending() throws InterruptedException {
        int threads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    latch.await();
                    TransferRequest req = new TransferRequest();
                    req.setSourceWalletId(sourceWallet.getId());
                    req.setDestinationWalletId(destWallet.getId());
                    req.setAmount(100L);
                    req.setCurrency("INR");

                    // Send 100 INR 100 times. Max that could succeed is 10.
                    transactionService.transfer("idem-c-" + index, req);
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    // This is expected due to optimistic locking contention
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        latch.countDown(); // Start all threads
        done.await(); // Wait for all to finish

        log.info("Concurrent transfers test complete. Success: {}, Fail: {}", successCount.get(), failCount.get());

        Wallet updatedSource = walletRepository.findById(sourceWallet.getId()).get();
        Wallet updatedDest = walletRepository.findById(destWallet.getId()).get();

        assertTrue(updatedSource.getBalance() >= 0, "Source balance should never be negative");
        assertEquals(1000L, updatedSource.getBalance() + updatedDest.getBalance(), "Total system money must be conserved");
        assertEquals(100L * successCount.get(), updatedDest.getBalance(), "Dest balance should exactly equal the number of successful transfers * 100");
    }
}
