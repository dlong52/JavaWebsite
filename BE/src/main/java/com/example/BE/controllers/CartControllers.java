package com.example.BE.controllers;

import com.example.BE.dto.AddToCartRequest;
import com.example.BE.dto.CartDTO;
import com.example.BE.dto.CartItemDTO;
import com.example.BE.dto.UpdateCartItemRequest;
import com.example.BE.services.CartServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
public class CartControllers {
    @Autowired
    private CartServices cartService;
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@RequestBody AddToCartRequest cartRequest) {
        CartDTO updatedCartDTO = cartService.addToCart(cartRequest.getUserId(), cartRequest.getProductId(), cartRequest.getQuantity(), cartRequest.getSize());
        if (updatedCartDTO != null) {
            return ResponseEntity.ok(updatedCartDTO);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable int userId) {
        CartDTO cartDTO = cartService.getCartByUserId(userId);
        if (cartDTO != null) {
            return new ResponseEntity<>(cartDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/update")
    public ResponseEntity<CartItemDTO> updateCartItem(@RequestBody UpdateCartItemRequest updateRequest) {
        CartItemDTO updatedCartItemDTO = cartService.updateCartItem(updateRequest.getCartItemId(), updateRequest.getQuantity(), updateRequest.getSize());
        if (updatedCartItemDTO != null) {
            return new ResponseEntity<>(updatedCartItemDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable int cartItemId) {
        boolean isDeleted = cartService.deleteCartItem(cartItemId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204: Successful Deletion, no content to return
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404: Item not found
        }
    }
}
