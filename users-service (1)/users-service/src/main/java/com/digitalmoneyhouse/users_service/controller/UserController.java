package com.digitalmoneyhouse.users_service.controller;

import com.digitalmoneyhouse.users_service.model.dto.UserAuthDto;
import com.digitalmoneyhouse.users_service.model.dto.RegisterRequest;
import com.digitalmoneyhouse.users_service.model.dto.RegisterResponse;
import com.digitalmoneyhouse.users_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /*
    @GetMapping
    public ResponseEntity<List<RegisterResponse>> getAllUsers() {
        List<RegisterResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    } */

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
}
