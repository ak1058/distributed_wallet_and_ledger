package com.wallet.wallet_service.exception;

import com.wallet.wallet_service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        String requestId = request.getHeader("X-Request-ID") != null ? request.getHeader("X-Request-ID") : UUID.randomUUID().toString();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                requestId
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex, WebRequest request) {
        String requestId = request.getHeader("X-Request-ID") != null ? request.getHeader("X-Request-ID") : UUID.randomUUID().toString();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INSUFFICIENT_FUNDS",
                ex.getMessage(),
                requestId
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex, WebRequest request) {
        String requestId = request.getHeader("X-Request-ID") != null ? request.getHeader("X-Request-ID") : UUID.randomUUID().toString();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_REQUEST",
                ex.getMessage(),
                requestId
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String requestId = request.getHeader("X-Request-ID") != null ? request.getHeader("X-Request-ID") : UUID.randomUUID().toString();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                "Invalid input parameters",
                requestId
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);
        String requestId = request.getHeader("X-Request-ID") != null ? request.getHeader("X-Request-ID") : UUID.randomUUID().toString();
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.toString() + (ex.getCause() != null ? " Cause: " + ex.getCause().toString() : ""),
                requestId
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
