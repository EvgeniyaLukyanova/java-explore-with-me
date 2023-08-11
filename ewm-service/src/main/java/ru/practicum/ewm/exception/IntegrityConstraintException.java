package ru.practicum.ewm.exception;

public class IntegrityConstraintException extends RuntimeException {
    public IntegrityConstraintException(String message) {
        super(message);
    }
}
