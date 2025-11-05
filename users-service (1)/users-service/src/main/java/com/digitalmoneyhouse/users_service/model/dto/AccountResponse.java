package com.digitalmoneyhouse.users_service.model.dto;

public record AccountResponse(
        Long accountId,
        String cvu,
        String alias
) {
}
