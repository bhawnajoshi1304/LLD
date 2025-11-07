package TicTacToe.Game;

import java.util.Objects;

public class Board{
    private static final Character EMPTY = ' ';
    private final int size;
    private final Character[][] grid;
    public Board(int size) {
        this.size = size;
        grid = new Character[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = EMPTY;
            }
        }
    }
    public int getSize(){
        return size;
    }
    public boolean isValidMove(Position pos) {
        return pos.row >= 0 && pos.row < size && pos.col >= 0 && pos.col < size
                && Objects.equals(grid[pos.row][pos.col], EMPTY);
    }
    public void makeMove(Position pos, Character symbol) {
        grid[pos.row][pos.col] = symbol;
    }
    public boolean hasGameStateChanged(GameContext context) {
        boolean hasEmptyCell = false;

        // Check rows and track empty cells
        for (int i = 0; i < size; i++) {
            Character[] row = grid[i];
            if (!Objects.equals(row[0], EMPTY) && isWinningLine(row)) {
                context.setCurrentState(new WonState(context.getCurrentState().getPlayer()));
                return true;
            }else{
                System.out.println(i+" row not");
            }

            // Check for any empty cells in the row
            for (int j = 0; j < size; j++) {
                if (Objects.equals(grid[i][j], EMPTY)) {
                    hasEmptyCell = true;
                }
            }
        }

        // Check columns
        for (int j = 0; j < size; j++) {
            Character first = grid[0][j];
            if (Objects.equals(first, EMPTY)) continue;

            boolean win = true;
            for (int i = 1; i < size; i++) {
                if (!Objects.equals(grid[i][j], first)) {
                    System.out.println(j+" column not ");
                    win = false;
                    break;
                }
            }

            if (win) {
                context.setCurrentState(new WonState(context.getCurrentState().getPlayer()));
                return true;
            }
        }

        // Check diagonals
        Character[] diag1 = new Character[size];
        Character[] diag2 = new Character[size];

        for (int i = 0; i < size; i++) {
            diag1[i] = grid[i][i];
            diag2[i] = grid[i][size - 1 - i];
        }

        if (!Objects.equals(diag1[0], EMPTY) && isWinningLine(diag1)) {
            context.setCurrentState(new WonState(context.getCurrentState().getPlayer()));
            return true;
        }else{
            System.out.println("diag1 not");
        }

        if (!Objects.equals(diag2[0], EMPTY) && isWinningLine(diag2)) {
            context.setCurrentState(new WonState(context.getCurrentState().getPlayer()));
            return true;
        }else{
            System.out.println("diag2 not");
        }

        // No win, check for draw
        if (!hasEmptyCell) {
            context.setCurrentState(new DrawState());
            return true;
        }
        return false;
    }


    private boolean isWinningLine(Character[] line) {
        Character first = line[0];
        for (Character s : line) {
            if (!Objects.equals(s, first)) {
                return false;
            }
        }
        return true;
    }
    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Character symbol = grid[i][j];
                System.out.print(" "+symbol+" ");
                if (j < size - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i < size - 1) {
                for (int j = 0; j < size; j++) {
                    System.out.print("---");
                    if (j < size - 1) {
                        System.out.print("+");
                    }else
                        System.out.print("\n");
                }
            }
        }
        System.out.println();
    }
}