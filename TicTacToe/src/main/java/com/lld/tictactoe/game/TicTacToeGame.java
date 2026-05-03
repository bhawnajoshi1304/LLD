package com.lld.tictactoe.game;

import com.lld.tictactoe.model.Board;
import com.lld.tictactoe.model.Player;
import com.lld.tictactoe.model.Position;
import com.lld.tictactoe.observer.GameContext;
import com.lld.tictactoe.strategy.PlayerStrategy;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeGame {
    private final Board board;
    private final List<Player> players;
    private final GameContext gameContext;
    private int currentPlayerIndex;

    public TicTacToeGame(int numberOfPlayers, List<PlayerStrategy> strategies, List<Character> symbols, int boardSize) {
        this.board = new Board(boardSize);
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;

        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player(symbols.get(i), strategies.get(i));
            players.add(player);
        }

        this.gameContext = new GameContext(players.get(0));
    }

    public void play() {
        System.out.println("Starting TicTacToe game!");
        board.printBoard();

        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println(currentPlayer.getSymbol() + "'s turn.");

            Position position = currentPlayer.getPlayerStrategy().makeMove(board);
            int row = position.row;
            int col = position.col;

            if (board.isValidMove(position)) {
                board.makeMove(position, currentPlayer.getSymbol());
                System.out.println(currentPlayer.getSymbol() + " placed at (" + row + ", " + col + ")");
                board.printBoard();

                if (board.hasGameStateChanged(gameContext)) {
                    System.out.println("Game over!");
                    break;
                } else {
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                }
            } else {
                System.out.println("Invalid move! Try again.");
            }
        }
    }
}
