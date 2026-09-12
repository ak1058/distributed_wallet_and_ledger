package com.wallet.wallet_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.wallet_service.dto.DepositRequest;
import com.wallet.wallet_service.dto.TransactionResponse;
import com.wallet.wallet_service.service.TransactionService;
import com.wallet.wallet_service.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@Import(com.wallet.wallet_service.config.SecurityConfig.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDepositValidation() throws Exception {
        UUID walletId = UUID.randomUUID();
        DepositRequest req = new DepositRequest();
        req.setAmount(0L); // Invalid amount
        req.setCurrency("USD");

        mockMvc.perform(post("/api/v1/wallets/" + walletId + "/deposit")
                .header("Idempotency-Key", "key-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
    void testDepositSuccess() throws Exception {
        UUID walletId = UUID.randomUUID();
        DepositRequest req = new DepositRequest();
        req.setAmount(100L); 
        req.setCurrency("USD");
        
        TransactionResponse res = new TransactionResponse();
        res.setStatus("COMPLETED");
        res.setAmount(100L);

        when(transactionService.deposit(eq("key-123"), eq(walletId), any(DepositRequest.class)))
                .thenReturn(res);

        mockMvc.perform(post("/api/v1/wallets/" + walletId + "/deposit")
                .header("Idempotency-Key", "key-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
