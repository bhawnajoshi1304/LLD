package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;

import java.util.ArrayList;
import java.util.List;

public class KingMoveStrategy implements MoveStrategy {

    public static final KingMoveStrategy INSTANCE = new KingMoveStrategy();

    private static final int[][] KING_MOVES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
    };

    private KingMoveStrategy() {}

    @Override
    public List<Position> possibleMoves(Position from, Board board) {
        Piece piece = board.getPiece(from);
        return MoveUtils.positionMoves(from, KING_MOVES, board, piece.getColor());
    }
}
