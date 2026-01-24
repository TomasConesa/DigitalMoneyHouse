package com.digitalmoneyhouse.account_service.model.dto;

import java.math.BigDecimal;

public record TransferenceCreateRequest(
        BigDecimal amount,
        String destination,
        String description
) {
}
