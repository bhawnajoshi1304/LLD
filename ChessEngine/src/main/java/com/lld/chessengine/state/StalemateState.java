package com.lld.chessengine.state;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Engine;

public class StalemateState implements GameState {
    @Override
    public void onEnter(Engine engine) {
        System.out.println("Stalemate! Game is a draw.");
    }

    @Override
    public void onExit(Engine engine) {
    }

    @Override
    public boolean canMove(Engine engine, Board board) {
        return false;
    }

    @Override
    public boolean isGameOver() {
        return true;
    }

    @Override
    public String getStateName() {
        return "STALEMATE";
    }
}
