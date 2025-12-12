package com.digitalmoneyhouse.users_service.service;

import com.digitalmoneyhouse.users_service.client.AccountClient;
import com.digitalmoneyhouse.users_service.exceptions.ForbiddenException;
import com.digitalmoneyhouse.users_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.users_service.exceptions.UnauthorizedException;
import com.digitalmoneyhouse.users_service.exceptions.ValidationException;
import com.digitalmoneyhouse.users_service.model.Role;
import com.digitalmoneyhouse.users_service.model.User;
import com.digitalmoneyhouse.users_service.model.dto.*;
import com.digitalmoneyhouse.users_service.repository.RoleRepository;
import com.digitalmoneyhouse.users_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountClient accountClient;
    private final PasswordEncoder encoder;

    public RegisterResponse registerUser(RegisterRequest req) {

        validateUser(req);

        User newUser = User.builder()
                .name(req.name())
                .lastName(req.lastName())
                .dni(req.dni())
                .email(req.email())
                .telephone(req.telephone())
                .password(encoder.encode(req.password()))
                .build();

        Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: ROLE_USER"));
        newUser.getRoles().add(defaultRole);

        User savedUser = userRepository.save(newUser);

        AccountResponse accountResponse = accountClient.createAccount(savedUser.getUserId());

        return mapToRegisterResponse(savedUser, accountResponse);
    }

    private void validateUser(RegisterRequest reqDto) {
        if (userRepository.existsByEmail(reqDto.email())) {
            throw new ValidationException("Email ya registrado.");
        }

        if (userRepository.existsByDni(reqDto.dni())) {
            throw new ValidationException("DNI ya registrado.");
        }
    }

    private RegisterResponse mapToRegisterResponse(User user, AccountResponse accountResponse) {
        return new RegisterResponse(
                user.getUserId(),
                user.getName(),
                user.getLastName(),
                user.getDni(),
                user.getEmail(),
                user.getTelephone(),
                accountResponse
        );
    }


    public RegisterResponse getUserByEmail(String email) {
        User userByEmail = userRepository.getUserByEmail(email);
        if(userByEmail == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con email: " + email);
        }

        AccountResponse accountResponse = accountClient.getAccountByUserId(userByEmail.getUserId());
        return mapToRegisterResponse(userByEmail, accountResponse);
    }

    public UserAuthDto getUserForAuth(String email) {
        User user = userRepository.getUserByEmail(email);
        if(user == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con email: " + email);
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .distinct()
                .toList();

        return new UserAuthDto(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                roles
        );
    }

    public RegisterResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        AccountResponse accountResponse = accountClient.getAccountByUserId(user.getUserId());

        return mapToRegisterResponse(user, accountResponse);
    }

    @Transactional
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    public RegisterResponse updateUser(Long userId, UpdateUser updateUser) {
        Long authenticatedUserId = Long.parseLong(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString()
        );

        if (!authenticatedUserId.equals(userId)) {
            throw new ForbiddenException("No tenés permiso para modificar este usuario");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        if (updateUser.name() != null) {
            String name = updateUser.name().trim();
            if (name.isBlank()) {
                throw new ValidationException("El nombre no puede estar vacío.");
            }
            user.setName(name);
        }

        if (updateUser.lastName() != null) {
            String lastName = updateUser.lastName().trim();
            if (lastName.isBlank()) {
                throw new ValidationException("El apellido no puede estar vacío.");
            }
            user.setLastName(lastName);
        }

        if (updateUser.telephone() != null) {
            String telephone = updateUser.telephone().trim();
            if (telephone.isBlank()) {
                throw new ValidationException("El teléfono no puede estar vacío.");
            }
            user.setTelephone(telephone);
        }

        if (updateUser.dni() != null) {
            String dni = updateUser.dni().trim();
            if (dni.isBlank()) {
                throw new ValidationException("El DNI no puede estar vacío.");
            }

            if (!dni.equals(user.getDni()) && userRepository.existsByDniAndUserIdNot(dni, user.getUserId())) {
                throw new ValidationException("DNI ya registrado.");
            }

            user.setDni(dni);
        }

        if (updateUser.email() != null) {
            String email = updateUser.email().trim();
            if (email.isBlank()) {
                throw new ValidationException("El email no puede estar vacío.");
            }

            if (!email.equals(user.getEmail()) && userRepository.existsByEmailAndUserIdNot(email, user.getUserId())) {
                throw new ValidationException("Email ya registrado.");
            }

            user.setEmail(email);
        }

        userRepository.save(user);

        AccountResponse accountResponse = accountClient.getAccountByUserId(user.getUserId());
        return mapToRegisterResponse(user, accountResponse);
    }

}
