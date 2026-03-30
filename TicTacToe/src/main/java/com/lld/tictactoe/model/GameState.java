package com.lld.tictactoe.model;

public abstract class GameState{
    private final Player player;
    public GameState(Player player){
        this.player = player;
    }
    public GameState(){
        this.player = null;
    }
    public Player getPlayer() {
        return player;
    }
    public abstract boolean isGameOver();
}
