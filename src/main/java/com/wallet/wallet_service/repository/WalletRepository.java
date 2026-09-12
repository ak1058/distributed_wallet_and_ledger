package com.wallet.wallet_service.repository;

import com.wallet.wallet_service.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    List<Wallet> findByUserId(UUID userId);
}
