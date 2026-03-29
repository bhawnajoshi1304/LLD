package main.java.com.parkingLot.model.factory;

import main.java.com.parkingLot.observer.DisplayBoard;
import main.java.com.parkingLot.observer.LoggingSystem;
import main.java.com.parkingLot.observer.UserPaymentSystem;
import main.java.com.parkingLot.strategy.FeeStrategy;

public class ParkingLotManagerFactory {
    public static ParkingLotManager create(FeeStrategy feeStrategy) {
        ParkingLotManager parkingLotManager = new ParkingLotManager();

        LoggingSystem loggingSystem = new LoggingSystem("lotTesting");
        UserPaymentSystem userPaymentSystem = new UserPaymentSystem(feeStrategy);

        DisplayBoard displayBoard = new DisplayBoard();
        parkingLotManager.registerExit(loggingSystem);
        parkingLotManager.registerExit(userPaymentSystem);
        parkingLotManager.registerExit(displayBoard);

        parkingLotManager.registerEntry(loggingSystem);
        parkingLotManager.registerEntry(displayBoard);
        return parkingLotManager;
    }
}
