package com.digitalmoneyhouse.account_service.model.dto;

import com.digitalmoneyhouse.account_service.model.CardType;
import lombok.Builder;

@Builder
public record CardResponse(
        Long cardId,
        String holderName,
        String brand,
        CardType type,
        String last4numbers,
        Integer expiryMonth,
        Integer expiryYear,
        Long accountId
) {}
