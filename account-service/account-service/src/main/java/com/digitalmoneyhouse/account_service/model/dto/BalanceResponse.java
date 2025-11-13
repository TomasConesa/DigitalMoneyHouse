package com.digitalmoneyhouse.account_service.model.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        Long accountId,
        BigDecimal balance
) {
}
