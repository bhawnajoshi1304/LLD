package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Color;

import java.util.ArrayList;
import java.util.List;

public class MoveUtils {
    public static List<Position> rayMoves(Position start, int[][] directions, Board board, Color pieceColor){
        List<Position> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
            Position current = start.offset(dx, dy);
            while (current!=null) {
                if (board.isEmpty(current)) {
                    moves.add(current);
                } else {
                    Piece target = board.getPiece(current);
                    if (target.getColor() != pieceColor) {
                        moves.add(current);
                    }
                    break;
                }
                current = current.offset(dx, dy);
            }
        }
        return moves;
    }
    public static List<Position> positionMoves(Position start, int[][] directions, Board board, Color pieceColor){
        List<Position> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
            Position current = start.offset(dx, dy);
            if (current!=null) {
                if (board.isEmpty(current)) {
                    moves.add(current);
                } else {
                    Piece target = board.getPiece(current);
                    if (target.getColor() != pieceColor) {
                        moves.add(current);
                    }
                }
            }
        }
        return moves;
    }
}
