package com.wallet.wallet_service.controller;

import com.wallet.wallet_service.dto.CreateUserRequest;
import com.wallet.wallet_service.dto.UserResponse;
import com.wallet.wallet_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public java.util.List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}
