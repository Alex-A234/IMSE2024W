package com.imse.onlineshop.sql.services.exceptions;

public class MissingProductException extends Exception {
    public MissingProductException(String message) {
        super(message);
    }
}
