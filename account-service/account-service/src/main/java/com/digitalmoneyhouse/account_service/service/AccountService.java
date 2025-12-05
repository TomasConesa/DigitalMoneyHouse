package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.account_service.model.Account;
import com.digitalmoneyhouse.account_service.model.dto.AccountResponse;
import com.digitalmoneyhouse.account_service.model.dto.BalanceResponse;
import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.utils.AliasCvu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AliasCvu generator;

    public AccountResponse createAccount(Long userId) {
        String cvu = generator.generateCVU();
        String alias = generator.generateAlias();

        while (accountRepository.existsByCvu(cvu)) {
            cvu = generator.generateCVU();
        }
        while (accountRepository.existsByAlias(alias)) {
            alias = generator.generateAlias();
        }

        Account newAccount = Account.builder()
                .userId(userId)
                .cvu(cvu)
                .alias(alias)
                .balance(BigDecimal.ZERO)
                .build();

        Account savedAccount = accountRepository.save(newAccount);

        return new AccountResponse(
                savedAccount.getAccountId(),
                savedAccount.getCvu(),
                savedAccount.getAlias()
        );
    }

    public BalanceResponse getBalance(Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada para el usuario con id: " + userId));

        return new BalanceResponse(
                account.getAccountId(),
                account.getBalance()
        );
    }

    public AccountResponse getAccountByUserId(Long userId) {
        Account account = accountRepository.getAccountByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada para usuario con id: " + userId));

        return new AccountResponse(
                account.getAccountId(),
                account.getCvu(),
                account.getAlias()
        );
    }
}
