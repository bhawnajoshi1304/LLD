package com.lld.tictactoe.model;

public class WonState extends GameState {
    public WonState(Player player){
        super(player);
    }
    @Override
    public boolean isGameOver() { return true; }
}
