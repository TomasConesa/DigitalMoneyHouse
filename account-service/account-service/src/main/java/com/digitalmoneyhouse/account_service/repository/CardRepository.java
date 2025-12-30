package com.digitalmoneyhouse.account_service.repository;

import com.digitalmoneyhouse.account_service.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByLast4numbersAndBrandAndExpiryMonthAndExpiryYear(String last4numbers, String brand, Integer expiryMonth, Integer expiryYear);

    Optional<Card> findByAccount_AccountIdAndCardId(Long cardId, Long accountId);

    List<Card> findByAccount_AccountId(Long accountId);
}
