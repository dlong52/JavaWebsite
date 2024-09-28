package com.example.BE.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailResponseDTO {
    private int orderId;
    private String status;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String userName; // thông tin người dùng
    private List<OrderDetailDTO> orderDetails;

    public OrderDetailResponseDTO(int orderId, String status, LocalDateTime orderDate,
                                  BigDecimal totalAmount, String userName,
                                  List<OrderDetailDTO> orderDetails) {
        this.orderId = orderId;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.userName = userName;
        this.orderDetails = orderDetails;
    }

    // Getters và Setters
    public int getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getUserName() { return userName; }
    public List<OrderDetailDTO> getOrderDetails() { return orderDetails; }
}
