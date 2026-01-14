package com.digitalmoneyhouse.account_service.controller;

import com.digitalmoneyhouse.account_service.model.dto.*;
import com.digitalmoneyhouse.account_service.service.AccountService;
import com.digitalmoneyhouse.account_service.service.CardService;
import com.digitalmoneyhouse.account_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestParam Long userId) {
        AccountResponse response = accountService.createAccount(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long accountId) {
        BalanceResponse response = accountService.getBalance(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getMyAccount() {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        AccountResponse response = accountService.getAccountByUserId(Long.parseLong(userId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/info")
    public ResponseEntity<AccountResponse> getAccountInfo(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountInfoById(accountId));
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long accountId, @RequestBody UpdateAlias request) {
        return ResponseEntity.ok(accountService.updateAccount(accountId, request));
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<CardResponse> linkCardToAccount(@PathVariable Long accountId, @RequestParam Long cardId) {
        CardResponse cardResponse = cardService.linkCardToAccount(accountId, cardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponse);
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        cardService.deleteCard(accountId, cardId);
        return ResponseEntity.ok().body("Tarjeta eliminada correctamente");
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponse> getAccountByUserId(@PathVariable Long userId) {
        AccountResponse accountResponse = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok(accountResponse);
    }
}
