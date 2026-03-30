package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;

import java.util.List;

public interface MoveStrategy {

    List<Position> possibleMoves(Position fromPosition, Board board);
}
