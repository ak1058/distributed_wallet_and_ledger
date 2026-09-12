package com.wallet.wallet_service.controller;

import com.wallet.wallet_service.dto.CreateWalletRequest;
import com.wallet.wallet_service.dto.WalletResponse;
import com.wallet.wallet_service.dto.DepositRequest;
import com.wallet.wallet_service.dto.WithdrawRequest;
import com.wallet.wallet_service.dto.TransactionResponse;
import com.wallet.wallet_service.service.WalletService;
import com.wallet.wallet_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public WalletController(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return walletService.createWallet(request);
    }

    @GetMapping("/{walletId}")
    public WalletResponse getWallet(@PathVariable UUID walletId) {
        return walletService.getWallet(walletId);
    }

    @GetMapping("/{walletId}/balance")
    public WalletResponse getWalletBalance(@PathVariable UUID walletId) {
        return walletService.getWalletBalance(walletId);
    }

    @PostMapping("/{walletId}/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(
            @PathVariable UUID walletId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody DepositRequest request) {
        return transactionService.deposit(idempotencyKey, walletId, request);
    }

    @PostMapping("/{walletId}/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(
            @PathVariable UUID walletId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody WithdrawRequest request) {
        return transactionService.withdraw(idempotencyKey, walletId, request);
    }
}
