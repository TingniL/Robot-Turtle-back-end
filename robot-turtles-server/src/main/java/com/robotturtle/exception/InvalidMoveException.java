package com.robotturtle.exception;

import org.springframework.http.HttpStatus;

public class InvalidMoveException extends GameException {
    public InvalidMoveException(String message) {
        super(message, "INVALID_MOVE", HttpStatus.BAD_REQUEST);
    }
} 