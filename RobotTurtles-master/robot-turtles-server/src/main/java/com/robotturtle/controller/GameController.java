package com.robotturtle.controller;

import com.robotturtle.dto.CreateGameRequest;
import com.robotturtle.dto.GameActionRequest;
import com.robotturtle.dto.JoinGameRequest;
import com.robotturtle.model.*;
import com.robotturtle.model.enums.PlayerType;
import com.robotturtle.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {
    private final GameService gameService;

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody CreateGameRequest request) {
        GameConfig config = new GameConfig();
        config.setPlayerCount(request.getPlayerCount());
        config.setAiPlayerCount(request.getAiPlayerCount());
        
        Player hostPlayer = new Player(
            request.getHostPlayer().getName(),
            request.getHostPlayer().getColor(),
            PlayerType.HUMAN
        );
        config.setHostPlayer(hostPlayer);
        
        Game game = gameService.createGame(config);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{gameCode}/join")
    public ResponseEntity<Game> joinGame(
            @PathVariable String gameCode,
            @RequestBody JoinGameRequest request) {
        Player player = new Player(
            request.getPlayerName(),
            request.getPreferredColor(),
            PlayerType.HUMAN
        );
        
        Game game = gameService.joinGame(gameCode, player);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        return ResponseEntity.ok(game);
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<Game> startGame(@PathVariable String gameId) {
        Game game = gameService.startGame(gameId);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{gameId}/actions")
    public ResponseEntity<Game> executeAction(
            @PathVariable String gameId,
            @RequestBody GameActionRequest request) {
        
        request.setGameId(gameId); // 确保使用路径中的gameId
        
        Game game;
        switch (request.getActionType()) {
            case MOVE:
                List<Card> cards = request.getCards().stream()
                    .map(Card::new)
                    .collect(Collectors.toList());
                game = gameService.executeMove(gameId, request.getPlayerId(), cards);
                break;
                
            case PLACE_WALL:
                Wall wall = new Wall(request.getWall().getType());
                wall.setPosition(request.getWall().getPosition());
                game = gameService.placeWall(gameId, request.getPlayerId(), wall);
                break;
                
            case LASER:
                game = gameService.executeLaser(gameId, request.getPlayerId());
                break;
                
            case DRAW_CARDS:
                List<Card> drawnCards = gameService.drawCards(gameId, request.getPlayerId(), 5);
                game = gameService.getGame(gameId);
                break;
                
            case DISCARD_CARDS:
                List<Card> cardsToDiscard = request.getCards().stream()
                    .map(Card::new)
                    .collect(Collectors.toList());
                gameService.discardCards(gameId, request.getPlayerId(), cardsToDiscard);
                game = gameService.getGame(gameId);
                break;
                
            default:
                return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(game);
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> endGame(@PathVariable String gameId) {
        gameService.endGame(gameId);
        return ResponseEntity.ok().build();
    }
} 