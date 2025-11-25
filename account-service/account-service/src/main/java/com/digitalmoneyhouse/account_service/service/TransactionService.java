package com.digitalmoneyhouse.account_service.service;

import com.digitalmoneyhouse.account_service.repository.AccountRepository;
import com.digitalmoneyhouse.account_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


}
