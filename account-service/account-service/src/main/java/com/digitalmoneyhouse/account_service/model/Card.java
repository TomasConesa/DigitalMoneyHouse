package com.digitalmoneyhouse.account_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(nullable = false, length = 100)
    private String holderName;

    @Column(nullable = false, length = 30)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CardType type;

    @Column(nullable = false, length = 4)
    private String last4numbers;

    @Column(nullable = false)
    private Integer expiryMonth;

    @Column(nullable = false)
    private Integer expiryYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

}
