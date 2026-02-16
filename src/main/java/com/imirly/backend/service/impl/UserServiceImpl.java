package com.imirly.backend.service.impl;

import com.imirly.backend.dto.request.ChangePasswordRequest;
import com.imirly.backend.dto.request.RegisterRequest;
import com.imirly.backend.dto.request.UpdateUserRequest;
import com.imirly.backend.dto.response.UserResponse;
import com.imirly.backend.entity.User;
import com.imirly.backend.entity.enums.Role;
import com.imirly.backend.exception.BusinessException;
import com.imirly.backend.exception.ResourceNotFoundException;
import com.imirly.backend.repository.UserRepository;
import com.imirly.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        User user = User.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = findById(userId);
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = findById(userId);

        user.setNombre(request.getNombre());
        user.setTelefono(request.getTelefono());
        user.setFechaNacimiento(request.getFechaNacimiento());
        user.setDireccionCalle(request.getDireccionCalle());
        user.setDireccionCiudad(request.getDireccionCiudad());
        user.setDireccionCodigoPostal(request.getDireccionCodigoPostal());
        if (request.getFotoPerfilUrl() != null) {
            user.setFotoPerfilUrl(request.getFotoPerfilUrl());
        }

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findById(userId);

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        // Verificar que nueva y confirmación coincidan
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Las contraseñas nuevas no coinciden");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccionCalle(user.getDireccionCalle())
                .direccionCiudad(user.getDireccionCiudad())
                .direccionCodigoPostal(user.getDireccionCodigoPostal())
                .fotoPerfilUrl(user.getFotoPerfilUrl())
                .datosCompletos(user.tieneDatosCompletos())
                .createdAt(user.getCreatedAt())
                .build();
    }
}