package com.jagadesh.productQueryService.exception;

public class NonRetryableEventException extends RuntimeException {
    public NonRetryableEventException(String message) {
        super(message);
    }
}
