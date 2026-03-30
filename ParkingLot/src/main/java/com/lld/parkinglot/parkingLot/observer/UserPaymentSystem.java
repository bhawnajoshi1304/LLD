package main.java.com.lld.parkinglot.observer;

import main.java.com.lld.parkinglot.model.ParkingSession;
import main.java.com.lld.parkinglot.strategy.FeeStrategy;

public class UserPaymentSystem implements ParkingObserver {
    private final FeeStrategy feeStrategy;
    public UserPaymentSystem(FeeStrategy feeStrategy){
        this.feeStrategy = feeStrategy;
    }

    @Override
    public void onVehicleEntry(ParkingSession session) {
        return;
    }

    @Override
    public void onVehicleExit(ParkingSession session) {
        session.getUser().pay(feeStrategy.calculateFee(session.getSpot().getSpotType(),session.calculateDurationMinutes()));
    }
}