package com.robotturtle.dto;

import com.robotturtle.model.enums.PlayerColor;
import lombok.Data;

@Data
public class CreateGameRequest {
    private int playerCount;
    private int aiPlayerCount;
    private PlayerInfo hostPlayer;

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

    public PlayerInfo getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(PlayerInfo hostPlayer) {
        this.hostPlayer = hostPlayer;
    }

    @Data
    public static class PlayerInfo {
        private String name;
        private PlayerColor color;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PlayerColor getColor() {
            return color;
        }

        public void setColor(PlayerColor color) {
            this.color = color;
        }
    }
} 