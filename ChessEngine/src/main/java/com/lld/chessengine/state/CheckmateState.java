package com.lld.chessengine.state;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Engine;
import com.lld.chessengine.model.Color;

public class CheckmateState implements GameState {
    private final Color winner;

    public CheckmateState(Color winner) {
        this.winner = winner;
    }

    @Override
    public void onEnter(Engine engine) {
        System.out.println("Checkmate! " + winner + " wins!");
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
        return "CHECKMATE";
    }

    public Color getWinner() {
        return winner;
    }
}
