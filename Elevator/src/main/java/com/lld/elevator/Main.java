package com.lld.elevator;

import com.lld.elevator.model.Building;
import com.lld.elevator.model.Direction;
import com.lld.elevator.model.Elevator;
import com.lld.elevator.strategy.FCFSSchedulingStrategy;
import com.lld.elevator.strategy.ScanSchedulingStrategy;
import com.lld.elevator.strategy.LookSchedulingStrategy;

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
            String logPath = System.getProperty("user.dir") + "/../../logs/Elevator/elevator.log";
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
        System.out.println("Building: Office Tower (16 floors)");
        System.out.println("Strategies: FCFS vs SCAN vs LOOK");
        System.out.println();
        List<Elevator> elevators = new ArrayList<>();
        elevators.add(new Elevator(0, 16, new FCFSSchedulingStrategy()));
        elevators.add(new Elevator(1, 16, new ScanSchedulingStrategy()));
        elevators.add(new Elevator(2, 16, new LookSchedulingStrategy()));
        Building building = new Building("Office", 16, elevators);
        System.out.println("Elevator 0: FCFS (First-Come, First-Served)");
        System.out.println("Elevator 1: SCAN (Goes to min/max floors)");
        System.out.println("Elevator 2: LOOK (Goes to requested floors only)");
        System.out.println();
        System.out.println("=== PHASE 1: Initial Requests ===");
        System.out.println("Adding requests: 3^, 8v, 12^ (mixed directions)");
        building.addExternalRequest(3, Direction.UP);
        building.addExternalRequest(8, Direction.DOWN);
        building.addExternalRequest(12, Direction.UP);
        runSimulationSteps(building, 15);
        if (building.areAllElevatorsIdle()) {
            System.out.println("All requests completed. Ending simulation.");
            shutdownElevators(elevators);
            return;
        }
        System.out.println("=== PHASE 2: Mid-Simulation External Requests ===");
        System.out.println("Adding requests while elevators are busy: 15^, 2v");
        building.addExternalRequest(15, Direction.UP);
        building.addExternalRequest(2, Direction.DOWN);
        runSimulationSteps(building, 20);
        if (building.areAllElevatorsIdle()) {
            System.out.println("All requests completed. Ending simulation.");
            shutdownElevators(elevators);
            return;
        }
        System.out.println("=== PHASE 3: Edge Case - Same Direction Requests ===");
        System.out.println("Adding requests in current movement direction: 10^, 14^");
        building.addExternalRequest(10, Direction.UP);
        building.addExternalRequest(14, Direction.UP);
        runSimulationSteps(building, 15);
        if (building.areAllElevatorsIdle()) {
            System.out.println("All requests completed. Ending simulation.");
            shutdownElevators(elevators);
            return;
        }
        System.out.println("=== PHASE 4: Edge Case - Opposite Direction Requests ===");
        System.out.println("Adding requests opposite to current direction: 1v, 5v");
        building.addExternalRequest(1, Direction.DOWN);
        building.addExternalRequest(5, Direction.DOWN);
        runSimulationSteps(building, 20);
        if (building.areAllElevatorsIdle()) {
            System.out.println("All requests completed. Ending simulation.");
            shutdownElevators(elevators);
            return;
        }
        System.out.println("=== PHASE 5: Post-Completion Internal Requests ===");
        System.out.println("Adding internal requests after all external requests done: 7, 11");
        waitForAllIdle(building);
        System.out.println("Adding internal requests to each elevator:");
        elevators.get(0).addRequest(new com.lld.elevator.model.InternalRequest(7));
        elevators.get(1).addRequest(new com.lld.elevator.model.InternalRequest(11));
        elevators.get(2).addRequest(new com.lld.elevator.model.InternalRequest(4));
        runSimulationSteps(building, 15);
        if (building.areAllElevatorsIdle()) {
            System.out.println("All requests completed. Ending simulation.");
            shutdownElevators(elevators);
            return;
        }
        System.out.println("=== PHASE 6: Complex Mixed Scenario ===");
        System.out.println("Adding complex mixed requests to test all strategies:");
        System.out.println("External: 6^, 9v, 13^, 1v");
        System.out.println("Internal: Elevator0->8, Elevator1->2, Elevator2->15");
        building.addExternalRequest(6, Direction.UP);
        building.addExternalRequest(9, Direction.DOWN);
        building.addExternalRequest(13, Direction.UP);
        building.addExternalRequest(1, Direction.DOWN);
        elevators.get(0).addRequest(new com.lld.elevator.model.InternalRequest(8));
        elevators.get(1).addRequest(new com.lld.elevator.model.InternalRequest(2));
        elevators.get(2).addRequest(new com.lld.elevator.model.InternalRequest(15));
        runSimulationSteps(building, 30);
        System.out.println("=== SIMULATION COMPLETE ===");
        shutdownElevators(elevators);
    }

    private static void runManualMode() {
        System.out.println("=== Manual Mode ===");
        System.out.println("Building: Office Tower (16 floors)");
        System.out.println("Strategies: FCFS vs SCAN vs LOOK");
        System.out.println();
        List<Elevator> elevators = new ArrayList<>();
        elevators.add(new Elevator(0, 16, new FCFSSchedulingStrategy()));
        elevators.add(new Elevator(1, 16, new ScanSchedulingStrategy()));
        elevators.add(new Elevator(2, 16, new LookSchedulingStrategy()));
        Building building = new Building("Office", 16, elevators);
        System.out.println("Elevator 0: FCFS (First-Come, First-Served)");
        System.out.println("Elevator 1: SCAN (Goes to min/max floors)");
        System.out.println("Elevator 2: LOOK (Goes to requested floors only)");
        System.out.println();
        
        while (true) {
            System.out.println("\n1. Add External Request");
            System.out.println("2. Add Internal Request");
            System.out.println("3. Run Simulation Steps");
            System.out.println("4. View Elevator Status");
            System.out.println("5. Exit Manual Mode");
            System.out.print("Choose option: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addExternalRequest(building);
                    break;
                case "2":
                    addInternalRequest(elevators);
                    break;
                case "3":
                    runSimulationSteps(building, 5);
                    break;
                case "4":
                    viewElevatorStatus(elevators);
                    break;
                case "5":
                    System.out.println("Exiting Manual Mode...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addExternalRequest(Building building) {
        System.out.println("\n--- Add External Request ---");
        System.out.print("Enter floor (1-16): ");
        int floor = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter direction (UP/DOWN): ");
        String dir = scanner.nextLine().trim().toUpperCase();
        
        try {
            Direction direction = Direction.valueOf(dir);
            building.addExternalRequest(floor, direction);
            System.out.println("Request added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding request: " + e.getMessage());
        }
    }

    private static void addInternalRequest(List<Elevator> elevators) {
        System.out.println("\n--- Add Internal Request ---");
        System.out.print("Enter elevator number (0-2): ");
        int elevatorNum = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter destination floor (1-16): ");
        int floor = Integer.parseInt(scanner.nextLine().trim());
        
        try {
            if (elevatorNum >= 0 && elevatorNum < elevators.size()) {
                elevators.get(elevatorNum).addRequest(new com.lld.elevator.model.InternalRequest(floor));
                System.out.println("Request added successfully!");
            } else {
                System.out.println("Invalid elevator number.");
            }
        } catch (Exception e) {
            System.out.println("Error adding request: " + e.getMessage());
        }
    }

    private static void viewElevatorStatus(List<Elevator> elevators) {
        System.out.println("\n--- Elevator Status ---");
        for (Elevator e : elevators) {
            System.out.println("Elevator " + e.getId() + ": Floor " + e.getCurrentFloor() + 
                ", State: " + e.getState().getStateName() + 
                ", Pending Requests: " + e.hasPendingRequests());
        }
    }
    
    private static void shutdownElevators(List<Elevator> elevators) {
        System.out.println("Shutting down all elevators...");
        for (Elevator elevator : elevators) {
            elevator.stop();
        }
        System.out.println("All elevators shut down successfully.");
    }

    private static void runSimulationSteps(Building building, int maxSteps) {
        System.out.println("Running simulation for " + maxSteps + " steps...");
        for (int step = 1; step <= maxSteps; step++) {
            System.out.println("--- Step " + step + " ---");
            building.step();
            
            boolean allIdle = building.getElevators().stream()
                .allMatch(e -> !e.hasPendingRequests() && 
                             e.getState().getStateName().equals("IDLE"));
            if (allIdle) {
                System.out.println("All elevators idle with no pending requests.");
                break;
            }
        }
        System.out.println();
    }
    
    private static void waitForAllIdle(Building building) {
        System.out.println("Waiting for all elevators to become idle...");
        
        for (int step = 1; step <= 10; step++) {
            building.step();
            
            boolean allIdle = building.getElevators().stream()
                .allMatch(e -> !e.hasPendingRequests() && 
                             e.getState().getStateName().equals("IDLE"));
            
            if (allIdle) {
                System.out.println("All elevators are now idle.");
                return;
            }
        }
        System.out.println("Timeout waiting for elevators to become idle.");
    }

    public static void main(String[] args) {
        setupLogging();
        
        System.out.println("=== Elevator System ===");
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
