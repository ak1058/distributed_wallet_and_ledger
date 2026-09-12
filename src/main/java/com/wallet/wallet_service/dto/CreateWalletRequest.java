package com.wallet.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateWalletRequest {
    @NotNull
    private UUID userId;

    @NotBlank
    private String currency;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
