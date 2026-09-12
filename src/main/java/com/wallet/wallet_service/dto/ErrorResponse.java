package com.wallet.wallet_service.dto;

import java.time.ZonedDateTime;

public class ErrorResponse {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String requestId;

    public ErrorResponse(int status, String error, String message, String requestId) {
        this.timestamp = ZonedDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.requestId = requestId;
    }

    public ZonedDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
