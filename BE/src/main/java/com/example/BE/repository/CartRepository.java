package com.example.BE.repository;
import com.example.BE.models.Cart;
import com.example.BE.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUser_UserId(int userId);
}
