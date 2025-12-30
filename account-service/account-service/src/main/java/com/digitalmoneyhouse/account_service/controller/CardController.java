package com.digitalmoneyhouse.account_service.controller;

import com.digitalmoneyhouse.account_service.model.dto.CardCreateRequest;
import com.digitalmoneyhouse.account_service.model.dto.CardResponse;
import com.digitalmoneyhouse.account_service.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest cardRequest) {
        CardResponse cardResponse = cardService.createCard(cardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponse);
    }

    @GetMapping("/{accountId}/cards")
    public ResponseEntity<List<CardResponse>> getCardsByAccount(@PathVariable Long accountId) {
        List<CardResponse> cards = cardService.getCardsByAccount(accountId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<CardResponse> getCardOfAccount(@PathVariable Long accountId, @PathVariable Long cardId) {
        CardResponse cardResponse = cardService.getCardOfAccount(accountId, cardId);
        return ResponseEntity.ok(cardResponse);
    }
}
