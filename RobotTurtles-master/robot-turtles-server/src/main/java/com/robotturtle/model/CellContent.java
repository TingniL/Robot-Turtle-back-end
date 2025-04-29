package com.robotturtle.model;

import com.robotturtle.model.enums.CellType;
import lombok.Data;

@Data
public class CellContent {
    private CellType type;
    private Object details;

    public CellContent(CellType type) {
        this.type = type;
        this.details = null;
    }

    public CellContent(CellType type, Object details) {
        this.type = type;
        this.details = details;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }
} 