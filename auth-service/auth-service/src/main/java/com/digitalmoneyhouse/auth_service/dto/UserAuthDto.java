package com.digitalmoneyhouse.auth_service.dto;

import java.util.List;

public record UserAuthDto(
        Long id,
        String email,
        String password,
        List<String> roles
) {
}
