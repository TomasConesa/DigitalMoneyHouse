package com.digitalmoneyhouse.account_service.model.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long accountId,
        String cvu,
        String alias,
        BigDecimal balance
) {
}
