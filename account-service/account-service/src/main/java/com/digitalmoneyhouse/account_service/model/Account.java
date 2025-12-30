package com.digitalmoneyhouse.account_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true, length = 22)
    private String cvu;

    @Column(nullable = false, unique = true)
    private String alias;

    @Column(nullable = false)
    private BigDecimal balance;
}
