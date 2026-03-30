package com.lld.tictactoe.observer;

public abstract class GameState{
    private final com.lld.tictactoe.model.Player player;
    public GameState(com.lld.tictactoe.model.Player player){
        this.player = player;
    }
    public GameState(){
        this.player = null;
    }
    public com.lld.tictactoe.model.Player getPlayer() {
        return player;
    }
    public abstract boolean isGameOver();
}
