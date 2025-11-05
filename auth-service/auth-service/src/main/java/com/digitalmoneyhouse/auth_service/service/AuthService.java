package com.digitalmoneyhouse.auth_service.service;

import com.digitalmoneyhouse.auth_service.client.UserClient;
import com.digitalmoneyhouse.auth_service.dto.LoginRequest;
import com.digitalmoneyhouse.auth_service.dto.LoginResponse;
import com.digitalmoneyhouse.auth_service.dto.UserAuthDto;
import com.digitalmoneyhouse.auth_service.exceptions.JwtInvalidException;
import com.digitalmoneyhouse.auth_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.auth_service.exceptions.ValidationException;
import com.digitalmoneyhouse.auth_service.util.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        UserAuthDto user;

        try {
            // 1. Traer el usuario desde users-service
            user = userClient.getUserForAuth(request.email());
        } catch (feign.FeignException.NotFound e) {
            // Si users-service respondió 404
            throw new ResourceNotFoundException("Usuario no encontrado con mail: " + request.email());
        } catch (feign.FeignException e) {
            // Si users-service falló por otro motivo (500, timeout, etc.)
            throw new RuntimeException("Error al comunicar con users-service: " + e.getMessage(), e);
        }

        // 2. Validar contraseña
        if (!passwordEncoder.matches(request.password(), user.password())) {
            throw new ValidationException("Contraseña incorrecta");
        }

        // 3. Generar token JWT con los roles
        String token = jwtGenerator.generateToken(user.email(), user.roles());

        // 5. Devolver respuesta
        return new LoginResponse(token, user.roles());
    }

    public String logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ValidationException("Authorization header inválido");
        }

        String token = authHeader.substring(7).trim();

        if (!jwtGenerator.validateToken(token)) {
            throw new ValidationException("Token inválido o expirado");
        }

        return "Logout exitoso";
    }

}
