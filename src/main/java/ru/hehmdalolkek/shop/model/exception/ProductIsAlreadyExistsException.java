package ru.hehmdalolkek.shop.model.exception;

public class ProductIsAlreadyExistsException extends RuntimeException {
    public ProductIsAlreadyExistsException(String message) {
        super(message);
    }
}
