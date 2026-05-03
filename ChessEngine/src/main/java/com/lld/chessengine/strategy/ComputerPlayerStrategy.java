package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;
import com.lld.chessengine.model.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayerStrategy implements PlayerStrategy {
    private final String playerName;
    private final Color color;
    private final Random random;

    public ComputerPlayerStrategy(String playerName, Color color) {
        this.playerName = playerName;
        this.color = color;
        this.random = new Random();
    }

    @Override
    public Move getMove(Board board) {
        List<Move> validMoves = getAllValidMoves(board, color);
        
        if (validMoves.isEmpty()) {
            return null;
        }

        int index = random.nextInt(validMoves.size());
        Move move = validMoves.get(index);
        System.out.println(playerName + " plays: " + move.getFrom() + " " + move.getTo());
        return move;
    }

    private List<Move> getAllValidMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();
        
        for (Position from : PositionRegistry.allPositions()) {
            Piece piece = board.getPiece(from);
            if (piece != null && piece.getColor() == color) {
                List<Position> possibleDestinations = piece.getMoveStrategy().possibleMoves(from, board);
                for (Position to : possibleDestinations) {
                    if (to != null) {
                        moves.add(new Move(from, to));
                    }
                }
            }
        }
        
        return moves;
    }
}
