package com.robotturtle.model;

import lombok.Data;

@Data
public class GameConfig {
    private int playerCount;
    private int aiPlayerCount;
    private Player hostPlayer;

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getAiPlayerCount() {
        return aiPlayerCount;
    }

    public void setAiPlayerCount(int aiPlayerCount) {
        this.aiPlayerCount = aiPlayerCount;
    }

    public Player getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(Player hostPlayer) {
        this.hostPlayer = hostPlayer;
    }
} 