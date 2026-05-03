package com.lld.inventoryManagement;

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
            String logPath = System.getProperty("user.dir") + "/logs/InventoryManagement/inventory.log";
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
        System.out.println("Inventory Management System - Demo");
        System.out.println("This module is currently under development.");
        System.out.println("Demo will be implemented with sample inventory operations.");
        System.out.println("=== Demo Complete ===");
    }

    private static void runManualMode() {
        System.out.println("=== Manual Mode ===");
        System.out.println("Inventory Management System");
        System.out.println("This module is currently under development.");
        System.out.println("Manual mode will be implemented with interactive inventory operations.");
        
        while (true) {
            System.out.println("\n1. Add Product");
            System.out.println("2. Update Stock");
            System.out.println("3. View Inventory");
            System.out.println("4. Exit Manual Mode");
            System.out.print("Choose option: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    System.out.println("Add Product - Under development");
                    break;
                case "2":
                    System.out.println("Update Stock - Under development");
                    break;
                case "3":
                    System.out.println("View Inventory - Under development");
                    break;
                case "4":
                    System.out.println("Exiting Manual Mode...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void main(String[] args) {
        setupLogging();
        
        System.out.println("=== Inventory Management System ===");
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