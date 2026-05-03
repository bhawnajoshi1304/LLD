package com.lld.chessengine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String name;
    private Color color;
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
