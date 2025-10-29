package com.ecommerce.sportscenter.exceptions;

public class BasketAlreadyExistsException extends RuntimeException {
    public BasketAlreadyExistsException(String message) {
        super(message);
    }
}
