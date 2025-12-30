package com.digitalmoneyhouse.account_service.repository;

import com.digitalmoneyhouse.account_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    List<Transaction> findTop5ByAccountIdOrderByCreatedAtDesc(Long accountId);
}
