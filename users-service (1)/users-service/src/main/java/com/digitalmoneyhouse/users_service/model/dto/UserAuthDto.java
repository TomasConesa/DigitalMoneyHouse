package com.digitalmoneyhouse.users_service.model.dto;

import java.util.List;


public record UserAuthDto(
        Long id,
        String email,
        String password,
        List<String> roles
) {
}
