package com.robotturtle.model;

import com.robotturtle.model.enums.WallType;
import lombok.Data;

@Data
public class Wall {
    private WallType type;
    private Position position;

    public Wall() {
    }

    public Wall(WallType type) {
        this.type = type;
    }

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