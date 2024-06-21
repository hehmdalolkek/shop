package ru.hehmdalolkek.shop.model.exception;

public class ProductWithCurrentTitleIsAlreadyExistsException extends RuntimeException {

    public ProductWithCurrentTitleIsAlreadyExistsException(String message) {
        super(message);
    }

}
