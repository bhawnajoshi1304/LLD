package com.lld.chessengine.model;

import com.lld.chessengine.strategy.Move;
import com.lld.chessengine.strategy.PlayerStrategy;
import com.lld.chessengine.state.*;

public class Engine {
    Board board;
    Player whitePlayer, blackPlayer, currentPlayer;
    Status gameStatus = Status.ACTIVE;
    PlayerStrategy whiteStrategy, blackStrategy;
    GameState currentState;

    public void startGame(String name1, String name2, PlayerStrategy strategy1, PlayerStrategy strategy2) throws RuntimeException {
        whitePlayer = new Player(name1, Color.WHITE);
        blackPlayer = new Player(name2, Color.BLACK);
        whiteStrategy = strategy1;
        blackStrategy = strategy2;
        currentPlayer = whitePlayer;
        board = new Board();
        board.initializeBoard();
        board.printBoard();
        currentState = new ActiveState();
        currentState.onEnter(this);
        play();
    }

    public void play() {
        while (!currentState.isGameOver()) {
            PlayerStrategy currentStrategy = (currentPlayer == whitePlayer) ? whiteStrategy : blackStrategy;
            Move move = currentStrategy.getMove(board);

            if (move == null) {
                System.out.println("No valid moves available. Game over.");
                break;
            }

            if (board.movePiece(move.getFrom(), move.getTo(), currentPlayer.getColor())) {
                board.printBoard();
                checkGameState();
                if (!currentState.isGameOver()) {
                    togglePlayer();
                }
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
    }

    private void checkGameState() {
        Color opponentColor = (currentPlayer.getColor() == Color.WHITE) ? Color.BLACK : Color.WHITE;

        if (board.isInsufficientMaterial()) {
            System.out.println("Insufficient material! Game is a draw.");
            transitionToState(new StalemateState());
        } else if (board.isCheckmate(opponentColor)) {
            Color winner = currentPlayer.getColor();
            transitionToState(new CheckmateState(winner));
        } else if (board.isStalemate(opponentColor)) {
            transitionToState(new StalemateState());
        } else if (board.isKingInCheck(opponentColor)) {
            transitionToState(new CheckState(opponentColor));
        } else {
            transitionToState(new ActiveState());
        }
    }

    private void transitionToState(GameState newState) {
        if (currentState != null) {
            currentState.onExit(this);
        }
        currentState = newState;
        currentState.onEnter(this);
    }

    private void togglePlayer() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }
}
