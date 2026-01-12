package com.digitalmoneyhouse.account_service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AddMoneyRequest(
        @NotNull(message = "El cardId es obligatorio")
        Long cardId,

        @NotNull(message = "El monto es obligatorio")
        BigDecimal amount,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String description
) {
}
