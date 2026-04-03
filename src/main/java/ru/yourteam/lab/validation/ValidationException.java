package ru.yourteam.lab.validation;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
