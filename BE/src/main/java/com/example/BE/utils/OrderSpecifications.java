package com.example.BE.utils;

import com.example.BE.models.Order;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class OrderSpecifications {

    public static Specification<Order> withUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("username"), "%" + username + "%");
        };
    }
    public static Specification<Order> withPaymentMethod(String paymentMethod) {
        return (root, query, criteriaBuilder) -> {
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("payment").get("paymentMethod"), paymentMethod);
        };
    }
    public static Specification<Order> withStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Order> withOrderDateRange(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("orderDate"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate);
            }
        };
    }
}
