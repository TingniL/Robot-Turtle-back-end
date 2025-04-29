package com.robotturtle.controller;

import com.robotturtle.model.Game;
import com.robotturtle.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {
    private final GameService gameService;

    @MessageMapping("/game/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public Game getGameUpdates(@DestinationVariable String gameId) {
        return gameService.getGame(gameId);
    }
} 