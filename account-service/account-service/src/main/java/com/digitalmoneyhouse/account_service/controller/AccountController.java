package com.digitalmoneyhouse.account_service.controller;

import com.digitalmoneyhouse.account_service.model.dto.AccountResponse;
import com.digitalmoneyhouse.account_service.model.dto.BalanceResponse;
import com.digitalmoneyhouse.account_service.model.dto.TransactionResponse;
import com.digitalmoneyhouse.account_service.service.AccountService;
import com.digitalmoneyhouse.account_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestParam Long userId) {
        AccountResponse response = accountService.createAccount(userId);
        return ResponseEntity.ok(response);
    }
    /*
    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount() {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        AccountResponse response = accountService.createAccount(Long.parseLong(userId));
        return ResponseEntity.ok(response);
    }*/

    /*
    @GetMapping("/me/balance")
    public ResponseEntity<BalanceResponse> getMyBalance() {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        BalanceResponse response = accountService.getBalance(Long.parseLong(userId));

        return ResponseEntity.ok(response);
    }*/


    @GetMapping("/{accountId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long accountId) {
        BalanceResponse response = accountService.getBalance(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long accountId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getMyAccount() {
        String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        AccountResponse response = accountService.getAccountByUserId(Long.parseLong(userId));

        return ResponseEntity.ok(response);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponse> getAccountByUserId(@PathVariable Long userId) {
        AccountResponse accountResponse = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok(accountResponse);
    }
}
