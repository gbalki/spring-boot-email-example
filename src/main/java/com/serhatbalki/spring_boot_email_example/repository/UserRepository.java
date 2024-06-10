package com.serhatbalki.spring_boot_email_example.repository;

import com.serhatbalki.spring_boot_email_example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String email);
}
