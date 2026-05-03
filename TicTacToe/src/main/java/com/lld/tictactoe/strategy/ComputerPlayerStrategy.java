package com.lld.tictactoe.strategy;

import com.lld.tictactoe.model.Board;
import com.lld.tictactoe.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayerStrategy implements PlayerStrategy {
    private final String playerName;
    private final Random random;

    public ComputerPlayerStrategy(String playerName) {
        this.playerName = playerName;
        this.random = new Random();
    }

    @Override
    public Position makeMove(Board board) {
        List<Position> availableMoves = new ArrayList<>();

        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                if (board.isValidMove(pos)) {
                    availableMoves.add(pos);
                }
            }
        }

        if (!availableMoves.isEmpty()) {
            int index = random.nextInt(availableMoves.size());
            Position move = availableMoves.get(index);
            System.out.printf("%s plays at (%d, %d)%n", playerName, move.row, move.col);
            return move;
        }

        return new Position(0, 0);
    }
}
