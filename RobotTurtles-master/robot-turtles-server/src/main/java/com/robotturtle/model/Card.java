package com.robotturtle.model;

import com.robotturtle.model.enums.CardType;
import lombok.Data;

@Data
public class Card {
    private CardType type;

    public Card(CardType type) {
        this.type = type;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }
} 