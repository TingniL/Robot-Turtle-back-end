package com.robotturtle.exception;

import org.springframework.http.HttpStatus;

public class InvalidGameStateException extends GameException {
    public InvalidGameStateException(String message) {
        super(message, "INVALID_GAME_STATE", HttpStatus.BAD_REQUEST);
    }
} 