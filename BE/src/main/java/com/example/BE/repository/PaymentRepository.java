package com.example.BE.repository;
import com.example.BE.models.Payment;
import com.example.BE.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRepository extends JpaRepository<Payment, Integer>, JpaSpecificationExecutor<Product> {
}