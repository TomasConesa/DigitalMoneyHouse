package com.digitalmoneyhouse.account_service.model.dto;

import com.digitalmoneyhouse.account_service.model.CardType;
import jakarta.validation.constraints.*;

public record CardCreateRequest(
        @NotBlank  String holderName,

        @NotBlank String brand,

        @NotNull CardType type,

        @NotBlank @Pattern(regexp = "^[0-9]{13,19}$") String number,

        @Min(1) @Max(12) Integer expiryMonth,

        @Min(2026) Integer expiryYear
) {}
