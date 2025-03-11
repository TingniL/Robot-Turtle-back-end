package com.robotturtle.repository;

import com.robotturtle.model.Game;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final ConcurrentHashMap<String, Game> gamesById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Game> gamesByCode = new ConcurrentHashMap<>();

    @Override
    public Game save(Game game) {
        if (game.getId() == null) {
            game.setId(UUID.randomUUID().toString());
        }
        gamesById.put(game.getId(), game);
        if (game.getGameCode() != null) {
            gamesByCode.put(game.getGameCode(), game);
        }
        return game;
    }

    @Override
    public Optional<Game> findById(String id) {
        return Optional.ofNullable(gamesById.get(id));
    }

    @Override
    public Optional<Game> findByGameCode(String gameCode) {
        return Optional.ofNullable(gamesByCode.get(gameCode));
    }

    @Override
    public List<Game> findAll() {
        return new ArrayList<>(gamesById.values());
    }

    @Override
    public void deleteById(String id) {
        Game game = gamesById.remove(id);
        if (game != null && game.getGameCode() != null) {
            gamesByCode.remove(game.getGameCode());
        }
    }

    @Override
    public void deleteByGameCode(String gameCode) {
        Game game = gamesByCode.remove(gameCode);
        if (game != null) {
            gamesById.remove(game.getId());
        }
    }
} 