package com.robotturtle.dto;

import com.robotturtle.model.enums.PlayerColor;
import lombok.Data;

@Data
public class JoinGameRequest {
    private String playerName;
    private PlayerColor preferredColor;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public PlayerColor getPreferredColor() {
        return preferredColor;
    }

    public void setPreferredColor(PlayerColor preferredColor) {
        this.preferredColor = preferredColor;
    }
} 