package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.account_service.exceptions.ValidationException;
import com.digitalmoneyhouse.account_service.model.Account;
import com.digitalmoneyhouse.account_service.model.dto.AccountInfoResponse;
import com.digitalmoneyhouse.account_service.model.dto.AccountResponse;
import com.digitalmoneyhouse.account_service.model.dto.BalanceResponse;
import com.digitalmoneyhouse.account_service.model.dto.UpdateAlias;
import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.utils.AliasCvu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

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
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada para el usuario con id: " + userId));

        return new BalanceResponse(
                account.getAccountId(),
                account.getBalance()
        );
    }

    public AccountInfoResponse getAccountInfoById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        return new AccountInfoResponse(
                account.getAccountId(),
                account.getCvu(),
                account.getAlias(),
                account.getBalance()
        );
    }

    private static final Pattern ALIAS_PATTERN = Pattern.compile("^[a-z0-9]+\\.[a-z0-9]+\\.[a-z0-9]+$");

    public AccountInfoResponse updateAccount(Long accountId, UpdateAlias request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        if (request.alias() != null) {
            String alias = request.alias().trim().toLowerCase();

            if (alias.isBlank()) {
                throw new ValidationException("El alias no puede estar vacío.");
            }

            if (!ALIAS_PATTERN.matcher(alias).matches()) {
                throw new ValidationException(
                        "Alias inválido. El formato requerido es: palabra.palabra.palabra");
            }

            account.setAlias(alias);
        }

        accountRepository.save(account);

        return new AccountInfoResponse(
                account.getAccountId(),
                account.getCvu(),
                account.getAlias(),
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
