package TicTacToe.Game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLoggerListener implements GameEventListener {

    private final String logFilePath;

    public FileLoggerListener(String logFilePath) {
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

    @Override
    public void onMoveMade(Position position, Character symbol) {
        log("Move made: " + symbol + " at (" + position.row + ", " + position.col + ")");
    }

    @Override
    public void onGameStateChanged(GameState state) {
        log("Game state changed: " + state.toString());
    }

    private void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(logFilePath, true))) {
            out.println(message);
        } catch (IOException e) {
            System.err.println("Error writing to game log file: " + e.getMessage());
        }
    }
}
