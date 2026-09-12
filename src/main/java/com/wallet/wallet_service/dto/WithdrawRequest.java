package com.wallet.wallet_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WithdrawRequest {
    @NotNull
    @Min(1)
    private Long amount;

    @NotBlank
    private String currency;

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
