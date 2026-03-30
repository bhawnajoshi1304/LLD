package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;

import java.util.List;

public class BishopMoveStrategy implements MoveStrategy{
    public static final BishopMoveStrategy INSTANCE = new BishopMoveStrategy();
    private BishopMoveStrategy(){}

    private static final int[][] DIRECTIONS = {
            {1, 1}, {-1, 1}, {1, 1}, {1, -1}
    };
    @Override
    public List<Position> possibleMoves(Position fromPosition, Board board) {
        return MoveUtils.rayMoves(fromPosition, DIRECTIONS);
    }
}
