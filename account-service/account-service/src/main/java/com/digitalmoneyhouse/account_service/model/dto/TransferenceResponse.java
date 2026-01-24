package com.digitalmoneyhouse.account_service.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenceResponse(
        Long transferenceId,
        String destinationIdentifier,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
}
