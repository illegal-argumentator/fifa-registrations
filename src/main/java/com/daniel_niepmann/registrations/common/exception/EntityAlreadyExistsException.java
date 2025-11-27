package com.daniel_niepmann.registrations.common.exception;

// TODO handle this exception
public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
