package com.digitalmoneyhouse.users_service.model.dto;

public record UpdateUser(
        String name,
        String lastName,
        String dni,
        String email,
        String telephone
) {
}
