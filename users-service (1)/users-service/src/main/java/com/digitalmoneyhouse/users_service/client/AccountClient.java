package com.digitalmoneyhouse.users_service.client;

import com.digitalmoneyhouse.users_service.model.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PostMapping("/accounts/create")
    AccountResponse createAccount(@RequestParam Long userId);

    @GetMapping("/accounts/user/{userId}")
    AccountResponse getAccountByUserId(@PathVariable Long userId);
}
