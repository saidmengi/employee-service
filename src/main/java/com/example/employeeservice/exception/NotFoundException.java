package com.example.employeeservice.exception;

import lombok.Getter;

@Getter
public class AlreadyExistException extends Exception{
    private final String[] args;
    private final int errorCode;

    public AlreadyExistException(String message, int errorCode, String... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
}
