package com.imirly.backend.controller;

import com.imirly.backend.dto.request.ChangePasswordRequest;
import com.imirly.backend.dto.request.UpdateUserRequest;
import com.imirly.backend.dto.response.UserResponse;
import com.imirly.backend.security.UserDetailsImpl;
import com.imirly.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(userDetails.getId(), request));
    }

    @PostMapping("/me/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok().body("Contraseña actualizada correctamente");
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails.getId());
        return ResponseEntity.ok().body("Cuenta eliminada correctamente");
    }
}