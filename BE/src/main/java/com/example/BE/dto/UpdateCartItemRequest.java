package com.example.BE.dto;

public class UpdateCartItemRequest {
    private long cartItemId; // ID của CartItem
    private int quantity; // Số lượng mới
    private String size; // Kích thước mới

    // Getters và Setters
    public long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
