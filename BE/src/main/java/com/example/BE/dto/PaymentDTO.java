package com.example.BE.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;

public abstract class PaymentDTO {
    @Builder
    @AllArgsConstructor
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
    }
}
