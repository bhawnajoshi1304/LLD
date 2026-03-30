package com.lld.tictactoe.model;

public class DrawState extends GameState {
    @Override
    public boolean isGameOver() {
        return true;
    }
}
