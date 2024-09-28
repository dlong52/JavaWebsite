package com.example.BE.controllers;

import com.example.BE.dto.*;
import com.example.BE.models.Order;
import com.example.BE.models.OrderDetail;
import com.example.BE.repository.OrderRepository;
import com.example.BE.services.OrderServices;
import com.example.BE.utils.OrderSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/orders")
public class OrderControllers {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderServices orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        try {
            Order order = orderService.createOrder(orderRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("success", "Order was successfully created", order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("error", e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) String paymentMethod, // Thêm paymentMethod
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Specification<Order> spec = Specification.where(OrderSpecifications.withUsername(username))
                .and(OrderSpecifications.withStatus(status))
                .and(OrderSpecifications.withOrderDateRange(startDate, endDate))
                .and(OrderSpecifications.withPaymentMethod(paymentMethod)); // Thêm điều kiện lọc theo paymentMethod

        List<OrderResponseDTO> orders = orderService.getAllOrders(spec, pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable int orderId) {
        // Lấy chi tiết đơn hàng từ service
        OrderResponseDTO orderDetail = orderService.getOrderDetailById(orderId);
        return ResponseEntity.ok(orderDetail);
    }
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable int orderId,
            @RequestParam String status) {
        // Gọi service để cập nhật trạng thái đơn hàng
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(updatedOrder);
    }
}
