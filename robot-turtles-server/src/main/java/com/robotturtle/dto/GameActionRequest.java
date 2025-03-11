package com.robotturtle.dto;

import com.robotturtle.model.enums.CardType;
import com.robotturtle.model.enums.WallType;
import com.robotturtle.model.Position;
import lombok.Data;
import java.util.List;

@Data
public class GameActionRequest {
    private String gameId;
    private String playerId;
    private ActionType actionType;
    private List<CardType> cards;
    private WallPlacement wall;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public List<CardType> getCards() {
        return cards;
    }

    public void setCards(List<CardType> cards) {
        this.cards = cards;
    }

    public WallPlacement getWall() {
        return wall;
    }

    public void setWall(WallPlacement wall) {
        this.wall = wall;
    }

    @Data
    public static class WallPlacement {
        private WallType type;
        private Position position;

        public WallType getType() {
            return type;
        }

        public void setType(WallType type) {
            this.type = type;
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }
    }

    public enum ActionType {
        MOVE,
        PLACE_WALL,
        LASER,
        DRAW_CARDS,
        DISCARD_CARDS
    }
} 