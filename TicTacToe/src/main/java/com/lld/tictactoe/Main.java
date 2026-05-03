package com.lld.tictactoe;

import com.lld.tictactoe.game.TicTacToeGame;
import com.lld.tictactoe.strategy.ComputerPlayerStrategy;
import com.lld.tictactoe.strategy.HumanPlayerStrategy;
import com.lld.tictactoe.strategy.PlayerStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static void setupLogging() {
        try {
            String logPath = System.getProperty("user.dir") + "/logs/TicTacToe/tictactoe.log";
            FileHandler fileHandler = new FileHandler(logPath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Failed to setup logging: " + e.getMessage());
        }
    }

    private static boolean getChoice(String message) {
        System.out.println(message);
        while (true) {
            String choice = scanner.nextLine().trim().toUpperCase();
            if (choice.equals("Y") || choice.equals("D")) {
                return true;
            } else if (choice.equals("N") || choice.equals("M")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'Y/D' for Yes or 'N/M' for No.");
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

    private static void runDemoMode() {
        System.out.println("=== Running Demo Mode ===");
        System.out.println("Demo: 2 players, 3x3 board");
        
        List<PlayerStrategy> strategies = new ArrayList<>();
        List<Character> symbols = new ArrayList<>();
        
        symbols.add('O');
        symbols.add('X');
        strategies.add(new ComputerPlayerStrategy("Computer O"));
        strategies.add(new ComputerPlayerStrategy("Computer X"));
        
        TicTacToeGame game = new TicTacToeGame(2, strategies, symbols, 3);
        game.play();
        
        System.out.println("=== Demo Complete ===");
    }

    private static void runManualMode() {
        System.out.println("=== Manual Mode ===");
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

    public static void main(String[] args) {
        setupLogging();
        
        System.out.println("=== TicTacToe Game ===");
        boolean demoMode;
        
        if (args.length > 0 && (args[0].equalsIgnoreCase("--demo") || args[0].equalsIgnoreCase("-d"))) {
            demoMode = true;
        } else if (args.length > 0 && (args[0].equalsIgnoreCase("--manual") || args[0].equalsIgnoreCase("-m"))) {
            demoMode = false;
        } else {
            demoMode = getChoice("Run Demo Mode? (Y/D for Yes, N/M for Manual): ");
        }
        
        if (demoMode) {
            runDemoMode();
        } else {
            runManualMode();
        }
    }
}
