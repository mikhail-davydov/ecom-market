package com.mkhldvdv.ecommarket.exception;

public class ThrottlingException extends RuntimeException {

    public ThrottlingException(String message) {
        super(message);
    }

}
