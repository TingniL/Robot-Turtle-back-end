package com.robotturtle.repository;

import com.robotturtle.model.Game;
import java.util.Optional;
import java.util.List;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> findById(String id);
    Optional<Game> findByGameCode(String gameCode);
    List<Game> findAll();
    void deleteById(String id);
    void deleteByGameCode(String gameCode);
} 