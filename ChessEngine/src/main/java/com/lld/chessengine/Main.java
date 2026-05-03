package com.lld.chessengine;

import com.lld.chessengine.model.Color;
import com.lld.chessengine.model.Engine;
import com.lld.chessengine.strategy.ComputerPlayerStrategy;
import com.lld.chessengine.strategy.HumanPlayerStrategy;
import com.lld.chessengine.strategy.PlayerStrategy;

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
            String logPath = System.getProperty("user.dir") + "/../../logs/ChessEngine/chessengine.log";
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

    private static void runDemoMode() {
        System.out.println("=== Running Demo Mode ===");
        System.out.println("Demo: Players 'Alice' and 'Bob'");
        
        PlayerStrategy whiteStrategy = new ComputerPlayerStrategy("Alice", Color.WHITE);
        PlayerStrategy blackStrategy = new ComputerPlayerStrategy("Bob", Color.BLACK);
        
        Engine game = new Engine();
        try {
            game.startGame("Alice", "Bob", whiteStrategy, blackStrategy);
        } catch (RuntimeException e) {
            System.out.println("Runtime error occurred: " + e.getMessage());
            throw e;
        }
        
        System.out.println("=== Demo Complete ===");
    }

    private static void runManualMode() {
        System.out.println("=== Manual Mode ===");
        System.out.print("Enter Player 1 name: ");
        String player1Name = scanner.nextLine().trim();

        System.out.print("Enter Player 2 name: ");
        String player2Name = scanner.nextLine().trim();

        PlayerStrategy whiteStrategy = new HumanPlayerStrategy(player1Name);
        PlayerStrategy blackStrategy = new HumanPlayerStrategy(player2Name);
        
        Engine game = new Engine();
        try {
            game.startGame(player1Name, player2Name, whiteStrategy, blackStrategy);
        } catch (RuntimeException e) {
            System.out.println("Runtime error occurred: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        setupLogging();
        
        System.out.println("=== Chess Engine ===");
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
