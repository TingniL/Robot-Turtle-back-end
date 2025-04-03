package com.robotturtle.exception;

import org.springframework.http.HttpStatus;

public class GameNotFoundException extends GameException {
    public GameNotFoundException(String gameId) {
        super("Game not found with id: " + gameId, "GAME_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
} 