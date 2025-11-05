package com.digitalmoneyhouse.auth_service.controller;

import com.digitalmoneyhouse.auth_service.dto.LoginRequest;
import com.digitalmoneyhouse.auth_service.dto.LoginResponse;
import com.digitalmoneyhouse.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

   @PostMapping("/login")
   public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
       return ResponseEntity.ok(authService.login(request));
   }

  @PostMapping("/user/logout")
  public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String logout = authService.logout(authHeader);
        return ResponseEntity.ok(logout);
  }
}
