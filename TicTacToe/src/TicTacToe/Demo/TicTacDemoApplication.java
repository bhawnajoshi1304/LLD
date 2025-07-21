package TicTacToe.Demo;

import TicTacToe.Game.TicTacToeGame;
import TicTacToe.Player.HumanPlayerStrategy;
import TicTacToe.Player.PlayerStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicTacDemoApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean getChoice(String message) {
        System.out.println(message);
        while (true) {
            String choice = scanner.nextLine().trim().toUpperCase();
            if (choice.equals("Y")) {
                return true;
            } else if (choice.equals("N")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'Y' or 'N'.");
            }
        }
    }
    private static char getSymbol(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.length() != 1) {
                System.out.println("Invalid input. Please enter a single character.");
            } else {
                return input.charAt(0);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to TicTacToe Game!");
        boolean play = true;
        while(play){
            List<PlayerStrategy> strategies = new ArrayList<>();
            List<Character> symbols = new ArrayList<>();
            int numberOfPlayers, boardSize;
            boolean choice = getChoice("Do you want to play regular TicTac game? (Yes [Y] or No [N]): ");
            if (choice) {
                numberOfPlayers = 2;
                boardSize = 3;
                symbols.add('O');
                symbols.add('X');
                strategies.add(new HumanPlayerStrategy("Player O"));
                strategies.add(new HumanPlayerStrategy("Player X"));
            }else {
                System.out.print("Enter number of Players: ");
                numberOfPlayers = scanner.nextInt();
                scanner.nextLine();
                for(int i=0;i<numberOfPlayers;i+=1) {
                    char symbol = getSymbol("Enter symbol for Player " + (i + 1) + ": ");
                    if (symbols.contains(symbol)) {
                        System.out.println("Symbol already taken. Choose another one.");
                        i--; // retry this player
                        continue;
                    }
                    symbols.add(symbol);
                    strategies.add(new HumanPlayerStrategy("Player " + symbol));
                }
                System.out.print("Enter size of Board: ");
                boardSize = scanner.nextInt();
                scanner.nextLine();
            }
            TicTacToeGame game = new TicTacToeGame(numberOfPlayers, strategies, symbols, boardSize);
            game.play();
            play = getChoice("Do you want to play another game? (Yes [Y] or No [N]): ");
        }
    }
}