package com.wallet.wallet_service.service;

import com.wallet.wallet_service.domain.User;
import com.wallet.wallet_service.domain.Wallet;
import com.wallet.wallet_service.dto.CreateWalletRequest;
import com.wallet.wallet_service.dto.WalletResponse;
import com.wallet.wallet_service.exception.ResourceNotFoundException;
import com.wallet.wallet_service.repository.UserRepository;
import com.wallet.wallet_service.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                user,
                request.getCurrency(),
                "ACTIVE"
        );
        Wallet savedWallet = walletRepository.save(wallet);
        return mapToResponse(savedWallet);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return mapToResponse(wallet);
    }
    
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "wallet-balances", key = "#id")
    public WalletResponse getWalletBalance(UUID id) {
        // Simple read for now. Will be extended later.
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return mapToResponse(wallet);
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUser().getId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getStatus(),
                wallet.getCreatedAt()
        );
    }
}
