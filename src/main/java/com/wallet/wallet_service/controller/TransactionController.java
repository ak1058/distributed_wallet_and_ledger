package com.wallet.wallet_service.controller;

import com.wallet.wallet_service.dto.TransactionResponse;
import com.wallet.wallet_service.dto.TransferRequest;
import com.wallet.wallet_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(idempotencyKey, request);
    }
}
