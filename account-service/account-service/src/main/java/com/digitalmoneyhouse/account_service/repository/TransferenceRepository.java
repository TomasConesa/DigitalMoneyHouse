package com.digitalmoneyhouse.account_service.repository;

import com.digitalmoneyhouse.account_service.model.Transference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TransferenceRepository extends JpaRepository<Transference, Long> {

    List<Transference> findTop50ByOriginAccountIdOrderByCreatedAtDesc(Long originAccountId);
}
