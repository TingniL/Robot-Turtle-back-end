package com.robotturtle.model;

import com.robotturtle.model.enums.CellType;
import lombok.Data;

@Data
public class Cell {
    private Position position;
    private CellContent content;

    public Cell(Position position) {
        this.position = position;
        this.content = new CellContent(CellType.EMPTY);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public CellContent getContent() {
        return content;
    }

    public void setContent(CellContent content) {
        this.content = content;
    }
} 