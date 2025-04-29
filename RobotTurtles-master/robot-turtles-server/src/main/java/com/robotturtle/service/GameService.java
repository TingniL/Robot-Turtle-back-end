package com.robotturtle.service;

import com.robotturtle.model.*;
import java.util.List;

public interface GameService {
    // 游戏管理
    Game createGame(GameConfig config);
    Game joinGame(String gameCode, Player player);
    Game startGame(String gameId);
    Game getGame(String gameId);
    List<Game> getAllGames();
    
    // 游戏动作
    Game executeMove(String gameId, String playerId, List<Card> cards);
    Game placeWall(String gameId, String playerId, Wall wall);
    Game executeLaser(String gameId, String playerId);
    
    // 卡牌管理
    List<Card> drawCards(String gameId, String playerId, int count);
    void discardCards(String gameId, String playerId, List<Card> cards);
    
    // 游戏状态检查
    boolean isGameOver(String gameId);
    boolean isValidMove(String gameId, String playerId, List<Card> cards);
    boolean isValidWallPlacement(String gameId, String playerId, Wall wall);
    
    // 游戏清理
    void endGame(String gameId);
    void removeInactiveGames();
} 