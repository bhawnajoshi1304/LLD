package com.lld.elevator;

import com.lld.elevator.model.Building;
import com.lld.elevator.model.Direction;
import com.lld.elevator.model.Elevator;
import com.lld.elevator.strategy.FCFSSchedulingStrategy;
import com.lld.elevator.strategy.ScanSchedulingStrategy;
import com.lld.elevator.strategy.LookSchedulingStrategy;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE ELEVATOR STRATEGY COMPARISON ===");
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
        System.out.println("=== PHASE 2: Mid-Simulation External Requests ===");
        System.out.println("Adding requests while elevators are busy: 15^, 2v");
        building.addExternalRequest(15, Direction.UP);
        building.addExternalRequest(2, Direction.DOWN);
        runSimulationSteps(building, 20);
        System.out.println("=== PHASE 3: Edge Case - Same Direction Requests ===");
        System.out.println("Adding requests in current movement direction: 10^, 14^");
        building.addExternalRequest(10, Direction.UP);
        building.addExternalRequest(14, Direction.UP);
        runSimulationSteps(building, 15);
        System.out.println("=== PHASE 4: Edge Case - Opposite Direction Requests ===");
        System.out.println("Adding requests opposite to current direction: 1v, 5v");
        building.addExternalRequest(1, Direction.DOWN);
        building.addExternalRequest(5, Direction.DOWN);
        runSimulationSteps(building, 20);
        System.out.println("=== PHASE 5: Post-Completion Internal Requests ===");
        System.out.println("Adding internal requests after all external requests done: 7, 11");
        waitForAllIdle(building);
        System.out.println("Adding internal requests to each elevator:");
        elevators.get(0).addRequest(new com.lld.elevator.model.InternalRequest(7));
        elevators.get(1).addRequest(new com.lld.elevator.model.InternalRequest(11));
        elevators.get(2).addRequest(new com.lld.elevator.model.InternalRequest(4));
        runSimulationSteps(building, 15);
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
}
