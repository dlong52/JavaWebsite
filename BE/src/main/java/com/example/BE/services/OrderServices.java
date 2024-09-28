package com.example.BE.services;

import com.example.BE.dto.*;

import java.time.ZoneId;

import com.example.BE.exceptions.ResourceNotFoundException;
import com.example.BE.models.*;
import com.example.BE.repository.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServices {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Order createOrder(OrderRequestDTO orderRequestDTO) {
        validateOrderRequest(orderRequestDTO);

        int userId = orderRequestDTO.getUserId();
        String paymentMethod = orderRequestDTO.getPaymentMethod();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("No items in the cart");
        }
        BigDecimal totalAmount = calculateTotalAmount(cartItems);
        Payment payment = createPayment(paymentMethod);
        Order order = new Order();
        order.setUser(user);  // Set the fetched user
        order.setTotalAmount(totalAmount);
        if (Objects.equals(paymentMethod, "vnpay")) {
            order.setStatus("paid");
        }
        order.setStatus("pending");
        order.setPayment(payment);
        order.setCart(cart);
        order.setAddress(orderRequestDTO.getAddress());
        order.setPhoneNumber(orderRequestDTO.getPhoneNumber());
        order.setUsername(orderRequestDTO.getUsername());
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);
        saveOrderDetailsAndUpdateStock(cartItems, order);
        cartItemRepository.deleteAll(cartItems);
        return order;
    }

    private void validateOrderRequest(OrderRequestDTO orderRequestDTO) {
        if (orderRequestDTO.getUserId() == 0 || orderRequestDTO.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Missing required fields: userId or paymentMethod");
        }
    }

    private BigDecimal calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(1).subtract(
                                Optional.ofNullable(item.getProduct().getDiscount())
                                        .map(discount -> BigDecimal.valueOf(discount).divide(BigDecimal.valueOf(100)))
                                        .orElse(BigDecimal.ZERO)))
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.DOWN); // Round down to the nearest unit
    }

    private Payment createPayment(String paymentMethod) {
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("pending");
        paymentRepository.save(payment);
        return payment;
    }

    private void saveOrderDetailsAndUpdateStock(List<CartItem> cartItems, Order order) {
        Gson gson = new Gson(); // Tạo một đối tượng Gson để phân tích JSON

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            // Check stock availability
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Product '" + product.getName() + "' is out of stock or insufficient quantity");
            }
            // Update product stock and sales count
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setSalesCount(product.getSalesCount() + cartItem.getQuantity());
            productRepository.save(product);
            // Save order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setSize(cartItem.getSize());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(Math.floor((product.getPrice() * (1 - (product.getDiscount() / 100))) / 1000) * 1000);
            // Phân tích chuỗi JSON để lấy danh sách hình ảnh
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> imageList = gson.fromJson(product.getImages(), listType); // Phân tích chuỗi JSON thành danh sách

            // Lấy URL đầu tiên từ danh sách hình ảnh
            if (imageList != null && !imageList.isEmpty()) {
                orderDetail.setImage(imageList.get(0)); // Lấy URL đầu tiên
            } else {
                orderDetail.setImage(null); // Hoặc có thể thiết lập một giá trị mặc định
            }

            orderDetailRepository.save(orderDetail);
        }
    }

    public List<OrderResponseDTO> getOrdersByUserId(int userId, Pageable pageable) {
        // Tìm tất cả đơn hàng của người dùng dựa trên userId
        List<Order> orders = orderRepository.findByUser_UserId(userId, pageable).getContent();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getAllOrders(Specification<Order> spec, Pageable pageable) {
        List<Order> orders = orderRepository.findAll(spec, pageable).getContent();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderResponseDTO convertToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUser(order.getUser().getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setCart(order.getCart().getCartId());
        dto.setAddress(order.getAddress());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setUsername(order.getUsername());
        PaymentInfoDTO paymentInfo = new PaymentInfoDTO();
        paymentInfo.setPaymentId(order.getPayment().getPaymentId());
        paymentInfo.setPaymentMethod(order.getPayment().getPaymentMethod());
        paymentInfo.setPaymentStatus(order.getPayment().getPaymentStatus());
        Date paymentDate = order.getPayment().getPaymentDate();
        if (paymentDate != null)
            paymentInfo.setPaymentDate(paymentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        else
            paymentInfo.setPaymentDate(null);
        dto.setPaymentInfo(paymentInfo);
        List<OrderDetailDTO> orderDetails = order.getOrderDetails().stream()
                .map(detail -> {
                    OrderDetailDTO detailDTO = new OrderDetailDTO();
                    detailDTO.setProduct(detail.getProduct().getProductId());
                    detailDTO.setProductName(detail.getProduct().getName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setSize(detail.getSize());
                    detailDTO.setPrice(BigDecimal.valueOf(detail.getPrice()));
                    detailDTO.setImage(detail.getImage());
                    return detailDTO;
                }).collect(Collectors.toList());

        dto.setOrderDetails(orderDetails);

        return dto;
    }

    public OrderResponseDTO getOrderDetailById(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Chuyển đổi từ Entity sang DTO
        return convertToDTO(order);
    }

    public OrderResponseDTO updateOrderStatus(int orderId, String status) {
        // Tìm đơn hàng theo orderId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        // Cập nhật trạng thái mới
        order.setStatus(status);
        // Lưu thay đổi vào cơ sở dữ liệu
        orderRepository.save(order);
        // Trả về DTO với trạng thái mới
        return convertToDTO(order);
    }
}
