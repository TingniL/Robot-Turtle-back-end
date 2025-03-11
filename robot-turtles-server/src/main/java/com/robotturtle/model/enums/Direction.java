package com.robotturtle.model.enums;

import com.robotturtle.model.Position;

public enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public Direction turnLeft() {
        switch (this) {
            case NORTH: return WEST;
            case WEST: return SOUTH;
            case SOUTH: return EAST;
            case EAST: return NORTH;
            default: throw new IllegalStateException();
        }
    }

    public Direction turnRight() {
        switch (this) {
            case NORTH: return EAST;
            case EAST: return SOUTH;
            case SOUTH: return WEST;
            case WEST: return NORTH;
            default: throw new IllegalStateException();
        }
    }

    public Position getNextPosition(Position current) {
        switch (this) {
            case NORTH: return new Position(current.getX(), current.getY() - 1);
            case SOUTH: return new Position(current.getX(), current.getY() + 1);
            case EAST: return new Position(current.getX() + 1, current.getY());
            case WEST: return new Position(current.getX() - 1, current.getY());
            default: throw new IllegalStateException();
        }
    }
} 