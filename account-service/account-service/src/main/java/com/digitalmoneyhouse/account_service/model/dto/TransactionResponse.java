package com.digitalmoneyhouse.account_service.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String type,
        String description,
        LocalDateTime createdAt
) {
}
