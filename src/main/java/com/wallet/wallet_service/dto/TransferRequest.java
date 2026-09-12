package com.wallet.wallet_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class TransferRequest {
    @NotNull
    private UUID sourceWalletId;

    @NotNull
    private UUID destinationWalletId;

    @NotNull
    @Min(1)
    private Long amount;

    @NotBlank
    private String currency;

    public UUID getSourceWalletId() { return sourceWalletId; }
    public void setSourceWalletId(UUID sourceWalletId) { this.sourceWalletId = sourceWalletId; }
    public UUID getDestinationWalletId() { return destinationWalletId; }
    public void setDestinationWalletId(UUID destinationWalletId) { this.destinationWalletId = destinationWalletId; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
