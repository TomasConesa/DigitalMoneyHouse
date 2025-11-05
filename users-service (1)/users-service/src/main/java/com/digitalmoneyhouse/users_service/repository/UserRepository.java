package com.digitalmoneyhouse.users_service.repository;

import com.digitalmoneyhouse.users_service.model.User;
import com.digitalmoneyhouse.users_service.model.dto.RegisterResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    User getUserByEmail(String email);

    boolean existsByDni(String dni);

}
