package com.lld.parkinglot.model.factory;

import com.lld.parkinglot.observer.DisplayBoard;
import com.lld.parkinglot.observer.LoggingSystem;
import com.lld.parkinglot.observer.UserPaymentSystem;
import com.lld.parkinglot.strategy.FeeStrategy;

public class ParkingLotManagerFactory {
    public static ParkingLotManager create(FeeStrategy feeStrategy) {
        ParkingLotManager parkingLotManager = new ParkingLotManager();

        LoggingSystem loggingSystem = new LoggingSystem("../../logs/ParkingLot/parkinglot.log");
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
