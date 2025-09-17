package com.example.checkout.exception;

public class UnknownProductException extends RuntimeException {
    public UnknownProductException(String productName) {
        super("Unknown product: " + productName);
    }
}