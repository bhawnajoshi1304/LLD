package com.lld.elevator.observer;

import com.lld.elevator.state.ElevatorState;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ElevatorLogger implements ElevatorObserver, AutoCloseable {
    private final PrintWriter logWriter;
    private static int globalStepCounter = 0;

    public ElevatorLogger(int elevatorId, String buildingName) {

        try {
            String logDir = "../../logs/Elevator";
            java.io.File dir = new java.io.File(logDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String logFile = logDir + "/elevator_" + elevatorId + ".log";
            
            logWriter = new PrintWriter(new FileWriter(logFile, true));

            logWriter.println("=== Elevator " + elevatorId + " Activity Log ===");
            logWriter.println("Step | Type | Value");
            logWriter.println("------------------");
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize logger for elevator " + elevatorId, e);
        }
    }

    @Override
    public void onElevatorFloorChanged(int floor) {
        logWriter.println(globalStepCounter + " | FLOOR | " + floor);
        logWriter.flush();
    }

    @Override
    public void onElevatorStateChanged(ElevatorState state) {
        logWriter.println(globalStepCounter + " | STATE | " + state.getStateName());
        logWriter.flush();
    }
    
    public static void incrementStepCounter() {
        globalStepCounter++;
    }

    @Override
    public void close() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
