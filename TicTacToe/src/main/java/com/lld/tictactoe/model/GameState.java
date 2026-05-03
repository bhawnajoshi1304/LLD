package com.lld.tictactoe.model;

import lombok.Getter;

public abstract class GameState{
    @Getter
    private final Player player;
    public GameState(Player player){
        this.player = player;
    }
    public GameState(){
        this.player = null;
    }
    public abstract boolean isGameOver();
}
