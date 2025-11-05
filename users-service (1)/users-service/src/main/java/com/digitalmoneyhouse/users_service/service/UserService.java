package com.digitalmoneyhouse.users_service.service;

import com.digitalmoneyhouse.users_service.client.AccountClient;
import com.digitalmoneyhouse.users_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.users_service.exceptions.ValidationException;
import com.digitalmoneyhouse.users_service.model.Role;
import com.digitalmoneyhouse.users_service.model.User;
import com.digitalmoneyhouse.users_service.model.dto.AccountResponse;
import com.digitalmoneyhouse.users_service.model.dto.UserAuthDto;
import com.digitalmoneyhouse.users_service.model.dto.RegisterRequest;
import com.digitalmoneyhouse.users_service.model.dto.RegisterResponse;
import com.digitalmoneyhouse.users_service.repository.RoleRepository;
import com.digitalmoneyhouse.users_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

        // 🔹 Asignar rol por defecto
        Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: ROLE_USER"));
        newUser.getRoles().add(defaultRole);

        User savedUser = userRepository.save(newUser);

        // Llamar a account-service para crear cuenta
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

    public List<RegisterResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        // Mapeo cada User a RegisterReponse llamando a account-service
        return users.stream().map(user -> {
            AccountResponse accountResponse = accountClient.getAccountByUserId(user.getUserId());
            return mapToRegisterResponse(user, accountResponse);
        }).toList();
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

        // Convertir a lista para el dto UserAuthDto
        return new UserAuthDto(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                roles
        );
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

}
