package com.lld.tictactoe.strategy;

import com.lld.tictactoe.model.Board;
import com.lld.tictactoe.model.Position;

public interface PlayerStrategy{
    Position makeMove(Board board);
}
