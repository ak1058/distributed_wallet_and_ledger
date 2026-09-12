package com.wallet.wallet_service.service;

import com.wallet.wallet_service.domain.User;
import com.wallet.wallet_service.dto.CreateUserRequest;
import com.wallet.wallet_service.dto.UserResponse;
import com.wallet.wallet_service.exception.ResourceNotFoundException;
import com.wallet.wallet_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = new User(
                UUID.randomUUID(),
                request.getExternalId(),
                request.getName(),
                request.getEmail(),
                "ACTIVE"
        );
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getExternalId(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
