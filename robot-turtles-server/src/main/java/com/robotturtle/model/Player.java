package com.robotturtle.model;

import com.robotturtle.model.enums.CardType;
import com.robotturtle.model.enums.Direction;
import com.robotturtle.model.enums.PlayerColor;
import com.robotturtle.model.enums.PlayerType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private String id;
    private String name;
    private PlayerColor color;
    private PlayerType type;
    private Position position;
    private Direction direction;
    private List<Card> hand = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();
    private boolean isReady;

    public Player(String name, PlayerColor color, PlayerType type) {
        this.name = name;
        this.color = color;
        this.type = type;
        this.direction = Direction.SOUTH;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    // Helper methods
    public void turnLeft() {
        this.direction = this.direction.turnLeft();
    }

    public void turnRight() {
        this.direction = this.direction.turnRight();
    }

    public Position getNextPosition() {
        return this.direction.getNextPosition(this.position);
    }

    public boolean hasLaserCard() {
        return hand.stream().anyMatch(card -> card.getType() == CardType.LASER);
    }
} 