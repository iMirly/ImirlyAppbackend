package com.imirly.backend.service;

import com.imirly.backend.dto.request.ChangePasswordRequest;
import com.imirly.backend.dto.request.RegisterRequest;
import com.imirly.backend.dto.request.UpdateUserRequest;
import com.imirly.backend.dto.response.UserResponse;
import com.imirly.backend.entity.User;

public interface UserService {
    User register(RegisterRequest request);
    User findByEmail(String email);
    User findById(Long id);
    UserResponse getCurrentUser(Long userId);
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
    void deleteUser(Long userId);
    boolean existsByEmail(String email);
}