package com.digitalmoneyhouse.users_service.controller;

import com.digitalmoneyhouse.users_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.users_service.exceptions.ValidationException;
import com.digitalmoneyhouse.users_service.model.User;
import com.digitalmoneyhouse.users_service.model.dto.UpdateUser;
import com.digitalmoneyhouse.users_service.model.dto.UserAuthDto;
import com.digitalmoneyhouse.users_service.model.dto.RegisterRequest;
import com.digitalmoneyhouse.users_service.model.dto.RegisterResponse;
import com.digitalmoneyhouse.users_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email")
    public ResponseEntity<RegisterResponse> getUserByEmail(@RequestParam String email) {
        RegisterResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/me")
    public ResponseEntity<RegisterResponse> getMyUser() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RegisterResponse user = userService.getUserById(Long.parseLong(userId));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<String> addRoleToUser(@PathVariable Long userId, @RequestParam String roleName) {
        userService.addRoleToUser(userId, roleName);
        return ResponseEntity.ok("Rol " + roleName + " asignado al usuario con id " + userId);
    }

    @GetMapping("/auth")
    public ResponseEntity<UserAuthDto> getUserForAuth(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserForAuth(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisterResponse> getUserById(@PathVariable Long id) {
        RegisterResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RegisterResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUser user) {
        RegisterResponse response = userService.updateUser(id, user);
        return ResponseEntity.ok(response);
    }

}
