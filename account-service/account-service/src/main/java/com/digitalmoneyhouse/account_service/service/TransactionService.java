package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.account_service.model.Account;
import com.digitalmoneyhouse.account_service.model.Transaction;
import com.digitalmoneyhouse.account_service.model.dto.TransactionResponse;
import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getAccountId());

        return transactions.stream()
                .map(this::toDto)
                .toList();
    }

    public List<TransactionResponse> getLastFiveTransactions(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        List<Transaction> transactions = transactionRepository.findTop5ByAccountIdOrderByCreatedAtDesc(account.getAccountId());

        return transactions.stream()
                .map(this::toDto)
                .toList();
    }

    private TransactionResponse toDto(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
