package com.robotturtle.model;

import lombok.Data;

@Data
public class Position {
    private int x;
    private int y;

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isValid() {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public boolean isAdjacent(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y) == 1;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
} 