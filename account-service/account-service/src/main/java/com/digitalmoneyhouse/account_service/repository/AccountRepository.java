package com.digitalmoneyhouse.account_service.repository;

import com.digitalmoneyhouse.account_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> getAccountByUserId(Long userId);

    boolean existsByCvu(String cvu);

    boolean existsByAlias(String alias);
}
