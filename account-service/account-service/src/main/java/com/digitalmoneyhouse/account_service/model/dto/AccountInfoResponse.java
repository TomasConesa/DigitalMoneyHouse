package com.digitalmoneyhouse.account_service.model.dto;

import java.math.BigDecimal;

public record AccountInfoResponse(
        Long accountId,
        String cvu,
        String alias,
        BigDecimal balance
) {
}
