package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Board;

import java.util.Scanner;

public class HumanPlayerStrategy implements PlayerStrategy {
    private final String playerName;
    private final Scanner scanner;

    public HumanPlayerStrategy(String playerName) {
        this.playerName = playerName;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public Move getMove(Board board) {
        while (true) {
            System.out.println(playerName + "'s move (e.g., E2 E4): ");
            String fromStr = scanner.next();
            String toStr = scanner.next();

            try {
                Position from = PositionRegistry.get(fromStr.charAt(0), Integer.parseInt("" + fromStr.charAt(1)));
                Position to = PositionRegistry.get(toStr.charAt(0), Integer.parseInt("" + toStr.charAt(1)));
                return new Move(from, to);
            } catch (Exception e) {
                System.out.println("Invalid input format. Please use format like 'E2 E4'.");
                scanner.nextLine(); // Clear input buffer
            }
        }
    }
}
