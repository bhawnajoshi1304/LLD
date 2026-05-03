package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Color;

import java.util.List;

public class KnightMoveStrategy implements MoveStrategy{
    public static final KnightMoveStrategy INSTANCE = new KnightMoveStrategy();
    public static final int[][] DIRECTIONS = {
            {2,1}, {2,-1}, {1,2}, {-1,2}, {1,-2}, {-1,-2}, {-2,1}, {-2,-1}
    };
    @Override
    public List<Position> possibleMoves(Position fromPosition, Board board) {
        Piece piece = board.getPiece(fromPosition);
        return MoveUtils.positionMoves(fromPosition, DIRECTIONS, board, piece.getColor());
    }
}
