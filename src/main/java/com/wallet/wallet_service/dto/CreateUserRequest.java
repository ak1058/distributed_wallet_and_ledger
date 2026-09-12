package com.wallet.wallet_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    @Email
    private String email;

    private String externalId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
}
