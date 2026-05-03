package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;

public interface PlayerStrategy {
    Move getMove(Board board);
}
