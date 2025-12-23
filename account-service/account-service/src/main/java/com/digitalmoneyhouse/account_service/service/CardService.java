package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.exceptions.ConflictException;
import com.digitalmoneyhouse.account_service.exceptions.ResourceNotFoundException;
import com.digitalmoneyhouse.account_service.model.Account;
import com.digitalmoneyhouse.account_service.model.Card;
import com.digitalmoneyhouse.account_service.model.dto.CardCreateRequest;
import com.digitalmoneyhouse.account_service.model.dto.CardResponse;
import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public CardResponse createCard(CardCreateRequest cardRequest) {
        String last4 = cardRequest.number().substring(cardRequest.number().length() - 4);

        cardRepository.findByLast4numbersAndBrandAndExpiryMonthAndExpiryYear(
                last4,
                cardRequest.brand(),
                cardRequest.expiryMonth(),
                cardRequest.expiryYear()
        ).ifPresent(card -> {
            throw new ConflictException("La tarjeta ya existe en el sistema");
        });

        Card card = Card.builder()
                .holderName(cardRequest.holderName())
                .brand(cardRequest.brand())
                .type(cardRequest.type())
                .last4numbers(last4)
                .expiryMonth(cardRequest.expiryMonth())
                .expiryYear(cardRequest.expiryYear())
                .build();

        Card cardSaved = cardRepository.save(card);
        return mapCardToResponse(cardSaved);
    }

    @Transactional
    public CardResponse linkCardToAccount(Long accountId, Long cardId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("La cuenta no existe"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("La tarjeta no existe"));

        if (card.getAccount() != null && !card.getAccount().getAccountId().equals(accountId)) {
            throw new ConflictException("La tarjeta ya está asociada a otra cuenta");
        }
        card.setAccount(account);

        Card cardSaved = cardRepository.save(card);
        return mapCardToResponse(cardSaved);
    }

    private CardResponse mapCardToResponse(Card card) {
        return CardResponse.builder()
                .cardId(card.getCardId())
                .holderName(card.getHolderName())
                .brand(card.getBrand())
                .type(card.getType())
                .last4numbers(card.getLast4numbers())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .accountId(card.getAccount() != null ? card.getAccount().getAccountId() : null)
                .build();
    }

    public List<CardResponse> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(this::mapCardToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getCardsByAccount(Long accountId) {

        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("La cuenta no existe"));

        List<Card> cards = cardRepository.findByAccount_AccountId(accountId);

        return cards.stream()
                .map(this::mapCardToResponse)
                .toList();
    }

    public CardResponse getCardOfAccount(Long accountId, Long cardId) {
        
        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("La cuenta no existe"));

        Card card = cardRepository.findByAccount_AccountIdAndCardId(accountId, cardId)
                .orElseThrow(() -> new ResourceNotFoundException("La tarjeta no existe o no pertenece a esta cuenta"));

        return mapCardToResponse(card);
    }
}
