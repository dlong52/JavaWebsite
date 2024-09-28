package com.example.BE.repository;
import com.example.BE.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByUsername(String username);
}

