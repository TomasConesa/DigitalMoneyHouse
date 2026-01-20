package com.digitalmoneyhouse.account_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "transferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferenceId;

    @Column(name = "origin_account_id", nullable = false)
    private Long originAccountId;

    @Column(name = "destination_account_id", nullable = false)
    private Long destinationAccountId;

    // cvu, cbu o alias
    @Column(name = "destination_identifier", nullable = false)
    private String destinationIdentifier;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
