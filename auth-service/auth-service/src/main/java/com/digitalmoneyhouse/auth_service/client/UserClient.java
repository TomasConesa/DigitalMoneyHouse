package com.digitalmoneyhouse.auth_service.client;

import com.digitalmoneyhouse.auth_service.dto.UserAuthDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service")
public interface UserClient {

    @GetMapping("/users/email")
    UserAuthDto getUserByEmail(@RequestParam String email);

    @GetMapping("/users/auth")
    UserAuthDto getUserForAuth(@RequestParam String email);

}
