package com.lld.chessengine.state;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Engine;
import com.lld.chessengine.model.Color;

public class CheckState implements GameState {
    private final Color kingInCheck;

    public CheckState(Color kingInCheck) {
        this.kingInCheck = kingInCheck;
    }

    @Override
    public void onEnter(Engine engine) {
        System.out.println("King " + kingInCheck + " is in check!");
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
        return "CHECK_" + kingInCheck;
    }

    public Color getKingInCheck() {
        return kingInCheck;
    }
}
