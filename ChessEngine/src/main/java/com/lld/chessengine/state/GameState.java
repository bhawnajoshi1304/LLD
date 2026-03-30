package com.lld.chessengine.state;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Engine;

public interface GameState {
    void onEnter(Engine engine);
    void onExit(Engine engine);
    boolean canMove(Engine engine, Board board);
    boolean isGameOver();
    String getStateName();
}
