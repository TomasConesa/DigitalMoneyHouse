package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.exceptions.ForbiddenException;
import com.digitalmoneyhouse.account_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.account_service.exceptions.ValidationException;
import com.digitalmoneyhouse.account_service.model.Account;
import com.digitalmoneyhouse.account_service.model.Card;
import com.digitalmoneyhouse.account_service.model.Transaction;
import com.digitalmoneyhouse.account_service.model.dto.AddMoneyRequest;
import com.digitalmoneyhouse.account_service.model.dto.TransactionResponse;
import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.repository.CardRepository;
import com.digitalmoneyhouse.account_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        if (!account.getUserId().equals(Long.parseLong(userId))) {
            throw new ForbiddenException("No tenés permisos para acceder a esta cuenta");
        }

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getAccountId());

        return transactions.stream()
                .map(this::toDto)
                .toList();
    }

    public List<TransactionResponse> getLastFiveTransactions(Long accountId) {

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        if (!account.getUserId().equals(Long.parseLong(userId))) {
            throw new ForbiddenException("No tenés permisos para acceder a esta cuenta");
        }

        List<Transaction> transactions = transactionRepository.findTop5ByAccountIdOrderByCreatedAtDesc(account.getAccountId());

        return transactions.stream()
                .map(this::toDto)
                .toList();
    }

    public TransactionResponse getTransactionDetail(Long transferId, Long accountId) {

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        if (!account.getUserId().equals(Long.parseLong(userId))) {
            throw new ForbiddenException("No tenés permisos para acceder a esta cuenta");
        }

        Transaction transaction = transactionRepository.findByIdAndAccountId(transferId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Transferencia no encontrada con id: " + transferId));

        return toDto(transaction);
    }

    @Transactional
    public TransactionResponse addMoneyFromCard(Long accountId, AddMoneyRequest request) {

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        if (!account.getUserId().equals(Long.parseLong(userId))) {
            throw new ForbiddenException("No tenés permisos para acceder a esta cuenta");
        }

        if (request.amount().signum() <= 0) {
            throw new ValidationException("El monto debe ser mayor a 0");
        }

        Card card = cardRepository.findById(request.cardId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada con id: " + request.cardId()));

        if (card.getAccount() == null || !card.getAccount().getAccountId().equals(accountId)) {
            throw new ForbiddenException("La tarjeta no pertenece a esta cuenta");
        }

        account.setBalance(account.getBalance().add(request.amount()));

        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .amount(request.amount())
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        return toDto(savedTransaction);
    }

    private TransactionResponse toDto(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
