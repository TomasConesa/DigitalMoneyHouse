package com.digitalmoneyhouse.users_service.model.dto;

import com.digitalmoneyhouse.users_service.client.AccountClient;

public record RegisterResponse(
        Long id,
        String name,
        String lastName,
        String dni,
        String email,
        String telephone,
        AccountResponse accountResponse
) {
}
