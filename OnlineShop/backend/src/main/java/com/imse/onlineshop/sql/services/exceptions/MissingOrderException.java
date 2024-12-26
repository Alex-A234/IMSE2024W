package com.imse.onlineshop.sql.services.exceptions;

public class MissingOrderException extends Exception {
    public MissingOrderException(String message) {
        super(message);
    }
}
