package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.exceptions.ForbiddenException;
import com.digitalmoneyhouse.account_service.exceptions.InsufficientFundsException;
import com.digitalmoneyhouse.account_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.account_service.exceptions.ValidationException;
import com.digitalmoneyhouse.account_service.model.Account;
import com.digitalmoneyhouse.account_service.model.Transference;
import com.digitalmoneyhouse.account_service.model.dto.RecipientResponse;
import com.digitalmoneyhouse.account_service.model.dto.TransferenceCreateRequest;
import com.digitalmoneyhouse.account_service.model.dto.TransferenceResponse;
import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.repository.TransferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransferenceService {

    private final AccountRepository accountRepository;
    private final TransferenceRepository transferenceRepository;

    @Transactional
    public TransferenceResponse createTransference(Long accountId, TransferenceCreateRequest tRequest) {
        Account originAccount = getAccountOwned(accountId);

        if (tRequest == null) throw new ResourceNotFoundException("Faltan datos para realizar la transferencia");
        if (tRequest.amount() == null || tRequest.amount().signum() <= 0) {
            throw new ValidationException("El monto debe ser mayor a 0");
        }
        if (tRequest.destination() == null || tRequest.destination().trim().isBlank()) {
            throw new ValidationException("El destinatario es obligatorio");
        }

        String destination = tRequest.destination().toLowerCase();

        Account destAccount = destinationAccount(destination)
                .orElseThrow(() -> new ValidationException("Cuenta inexistente"));

        if (destAccount.getAccountId().equals(originAccount.getAccountId())) {
            throw new ValidationException("No te puedes transferir a vos mismo");
        }

        if (originAccount.getBalance().signum() <= 0) {
            throw new InsufficientFundsException("Fondos insuficientes");
        }

        originAccount.setBalance(originAccount.getBalance().subtract(tRequest.amount()));
        destAccount.setBalance(destAccount.getBalance().add(tRequest.amount()));

        accountRepository.save(originAccount);
        accountRepository.save(destAccount);

        Transference transference = Transference.builder()
                .originAccountId(originAccount.getAccountId())
                .destinationAccountId(destAccount.getAccountId())
                .destinationIdentifier(destination)
                .amount(tRequest.amount())
                .description(tRequest.description())
                .createdAt(LocalDateTime.now())
                .build();

        Transference savedTransference = transferenceRepository.save(transference);

        return new TransferenceResponse(
                savedTransference.getTransferenceId(),
                savedTransference.getDestinationIdentifier(),
                savedTransference.getAmount(),
                savedTransference.getDescription(),
                savedTransference.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<RecipientResponse> getLastRecipients(Long accountId) {
        Account account = getAccountOwned(accountId);

        List<Transference> lastTransferences = transferenceRepository.findTop50ByOriginAccountIdOrderByCreatedAtDesc(account.getAccountId());

        Set<Long> recipients = new HashSet<>();
        List<RecipientResponse> responseList = new ArrayList<>();

        for (Transference t : lastTransferences) {
            Long destinationId = t.getDestinationAccountId();

            if (recipients.add(destinationId)) {
                responseList.add(new RecipientResponse(t.getDestinationIdentifier(), t.getCreatedAt()));

                if (responseList.size() == 10) break;
            }
        }
        return responseList;
    }

    private Optional<Account> destinationAccount(String destination) {
        if (destination.contains(".")) {
            return accountRepository.findByAlias(destination);
        }

        return accountRepository.findByCvu(destination);
    }

    private Account getAccountOwned(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));

        if (!account.getUserId().equals(currentUserId())) {
            throw new ForbiddenException("Permisos insuficientes");
        }

        return account;
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ForbiddenException("Permisos insuficientes");
        }

        String principal = auth.getPrincipal().toString();
        if ("anonymousUser".equals(principal) || principal.isBlank()) {
            throw new ForbiddenException("Permisos insuficientes");
        }

        try {
            return Long.valueOf(principal);
        } catch (NumberFormatException e) {
            throw new ForbiddenException("Permisos insuficientes");
        }
    }
}
