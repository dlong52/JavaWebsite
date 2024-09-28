package com.example.BE.services;

import com.example.BE.models.CartItem;
import com.example.BE.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemServices {

    @Autowired
    private CartItemRepository cartItemRepository;

    public Optional<CartItem> findById(Long id) {
        return cartItemRepository.findById(Math.toIntExact(id));
    }

    public void save(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }

    public void delete(Long id) {
        cartItemRepository.deleteById(Math.toIntExact(id));
    }
}
