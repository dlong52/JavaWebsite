package com.example.BE.repository;
import com.example.BE.models.Cart;
import com.example.BE.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart(Cart cart);
}
