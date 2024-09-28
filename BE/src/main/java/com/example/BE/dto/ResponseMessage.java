package com.example.BE.dto;

public class ResponseMessage {
    private String status;
    private String message;
    private Object data;

    public ResponseMessage(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
}
