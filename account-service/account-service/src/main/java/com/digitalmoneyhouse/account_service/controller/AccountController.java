package com.digitalmoneyhouse.account_service.controller;

import com.digitalmoneyhouse.account_service.model.dto.AccountResponse;
import com.digitalmoneyhouse.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestParam Long userId) {
        AccountResponse response = accountService.createAccount(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponse> getAccountByUserId(@PathVariable Long userId) {
        AccountResponse accountResponse = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok(accountResponse);
    }
}
