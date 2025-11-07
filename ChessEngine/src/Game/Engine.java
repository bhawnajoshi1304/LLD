package Game;

import Rules.Position;
import Rules.PositionRegistry;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Engine {
    Board board;
    Player whitePlayer, blackPlayer, currentPlayer;
    Status gameStatus = Status.ACTIVE;

    public void startGame(String name1, String name2) throws RuntimeException {
        whitePlayer = new Player(name1);
        blackPlayer = new Player(name2);
        currentPlayer = whitePlayer;
        board = new Board();
        board.initializeBoard();
        board.printBoard();
        play();
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);

        while (gameStatus == Status.ACTIVE) {
            System.out.println(currentPlayer.getName() + "'s move (e.g., E2 E4): ");
            String fromStr = scanner.next();
            String toStr = scanner.next();

            Position from = PositionRegistry.get(fromStr.charAt(0),parseInt(""+fromStr.charAt(1)));
            Position to = PositionRegistry.get(toStr.charAt(0),parseInt("" + toStr.charAt(1)));

            if (board.movePiece(from, to)) {
                board.printBoard();
                togglePlayer();
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
        scanner.close();
    }

    private void togglePlayer() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }
}

