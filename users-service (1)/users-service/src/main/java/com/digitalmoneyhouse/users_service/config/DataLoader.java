package com.digitalmoneyhouse.users_service.config;

import com.digitalmoneyhouse.users_service.model.Role;
import com.digitalmoneyhouse.users_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ROLE_USER"));
            roleRepository.save((new Role(null, "ROLE_ADMIN")));
        }
    }
}
