package com.digitalmoneyhouse.account_service.model.dto;

import java.time.LocalDateTime;

public record RecipientResponse(
        String destinationIdentifier,
        LocalDateTime lastTransferAt
) {
}
