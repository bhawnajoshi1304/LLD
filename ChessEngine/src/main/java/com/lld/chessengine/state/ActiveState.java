package com.lld.chessengine.state;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Engine;

public class ActiveState implements GameState {
    @Override
    public void onEnter(Engine engine) {
        System.out.println("Game is active");
    }

    @Override
    public void onExit(Engine engine) {
    }

    @Override
    public boolean canMove(Engine engine, Board board) {
        return true;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public String getStateName() {
        return "ACTIVE";
    }
}
