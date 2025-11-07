package main.java.com.parkingLot.observer;

import main.java.com.parkingLot.model.ParkingSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LoggingSystem implements ExitObserver, EntryObserver {

    private final String logFilePath;
    public LoggingSystem(String logFilePath) {
        this.logFilePath = logFilePath;
        File logFile = new File(logFilePath);
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create log directory: " + parentDir.getAbsolutePath());
            }
        }
    }

    private void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(logFilePath, true))) {
            out.println(message);
        } catch (IOException e) {
            System.err.println("Error writing to game log file: " + e.getMessage());
        }
    }

    @Override
    public void onVehicleEntry(ParkingSession session) {
        log(session.getUser().getVehicle().getNumberPlate()+" parked in spotNumber "+session.getSpot().getSpotId());
    }

    @Override
    public void onVehicleExit(ParkingSession session) {
        log(session.getUser().getVehicle().getNumberPlate()+" exited from spotNumber "+session.getSpot().getSpotId());
    }
}