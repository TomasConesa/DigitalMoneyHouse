package com.digitalmoneyhouse.account_service.controller;

import com.digitalmoneyhouse.account_service.model.dto.AddMoneyRequest;
import com.digitalmoneyhouse.account_service.model.dto.TransactionResponse;
import com.digitalmoneyhouse.account_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{accountId}/activity")
    public ResponseEntity<List<TransactionResponse>> getActivity(@PathVariable Long accountId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getLastFiveTransactions(@PathVariable Long accountId) {
        List<TransactionResponse> lastFive = transactionService.getLastFiveTransactions(accountId);
        return ResponseEntity.ok(lastFive);
    }

    @GetMapping("{accountId}/activity/{transferId}")
    public ResponseEntity<TransactionResponse> getTransactionDetail(@PathVariable Long transferId, @PathVariable Long accountId) {
        TransactionResponse transaction = transactionService.getTransactionDetail(transferId, accountId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{accountId}/transactions/deposit")
    public ResponseEntity<TransactionResponse> addMoneyFromCard(@PathVariable Long accountId, @Valid @RequestBody AddMoneyRequest request) {
        TransactionResponse transactionResponse = transactionService.addMoneyFromCard(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }
}
