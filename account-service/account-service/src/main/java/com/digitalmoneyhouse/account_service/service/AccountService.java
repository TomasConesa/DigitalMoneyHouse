package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.exceptions.ConflictException;
import com.digitalmoneyhouse.account_service.exceptions.ForbiddenException;
import com.digitalmoneyhouse.account_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.account_service.exceptions.ValidationException;
import com.digitalmoneyhouse.account_service.model.Account;
import com.digitalmoneyhouse.account_service.model.dto.AccountResponse;
import com.digitalmoneyhouse.account_service.model.dto.BalanceResponse;
import com.digitalmoneyhouse.account_service.model.dto.UpdateAlias;
import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.utils.AliasCvu;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AliasCvu generator;

    public AccountResponse createAccount(Long userId) {
        if (accountRepository.getAccountByUserId(userId).isPresent()) {
            throw new ConflictException("El usuario ya tiene una cuenta creada");
        }

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
                savedAccount.getAlias(),
                savedAccount.getBalance()
        );
    }

    public BalanceResponse getBalance(Long accountId) {
        Account account = getAccountOwned(accountId);

        return new BalanceResponse(
                account.getAccountId(),
                account.getBalance()
        );
    }

    public AccountResponse getAccountInfoById(Long accountId) {
        Account account = getAccountOwned(accountId);

        return new AccountResponse(
                account.getAccountId(),
                account.getCvu(),
                account.getAlias(),
                account.getBalance()
        );
    }

    private static final Pattern ALIAS_PATTERN = Pattern.compile("^[a-z0-9]+\\.[a-z0-9]+\\.[a-z0-9]+$");

    public AccountResponse updateAccount(Long accountId, UpdateAlias request) {
        Account account = getAccountOwned(accountId);

        if (request.alias() != null) {
            String alias = request.alias().trim().toLowerCase();

            if (alias.isBlank()) {
                throw new ValidationException("El alias no puede estar vacío.");
            }

            if (!ALIAS_PATTERN.matcher(alias).matches()) {
                throw new ValidationException("Alias inválido. El formato requerido es: palabra.palabra.palabra");
            }
            account.setAlias(alias);
        }

        Account saved = accountRepository.save(account);

        return new AccountResponse(
                saved.getAccountId(),
                saved.getCvu(),
                saved.getAlias(),
                saved.getBalance()
        );
    }

    public AccountResponse getAccountByUserId(Long userId) {
        if (!userId.equals(currentUserId())) {
            throw new ForbiddenException("No tenés permisos para acceder a esta cuenta");
        }

        Account account = accountRepository.getAccountByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada para usuario con id: " + userId));

        return new AccountResponse(account.getAccountId(), account.getCvu(), account.getAlias(), account.getBalance());
    }



    private Account getAccountOwned(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        if (!account.getUserId().equals(currentUserId())) {
            throw new ForbiddenException("No tenés permisos para acceder a esta cuenta");
        }

        return account;
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        String principal = auth.getPrincipal().toString();
        if ("anonymousUser".equals(principal) || principal.isBlank()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        try {
            return Long.valueOf(principal);
        } catch (NumberFormatException e) {
            throw new ForbiddenException("Usuario no autenticado");
        }
    }
}
