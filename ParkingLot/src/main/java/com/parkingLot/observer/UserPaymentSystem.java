package main.java.com.parkingLot.observer;

import main.java.com.parkingLot.model.ParkingSession;
import main.java.com.parkingLot.strategy.FeeStrategy;

public class UserPaymentSystem implements ExitObserver {
    private final FeeStrategy feeStrategy;
    public UserPaymentSystem(FeeStrategy feeStrategy){
        this.feeStrategy = feeStrategy;
    }
    @Override
    public void onVehicleExit(ParkingSession session) {
        session.getUser().pay(feeStrategy.calculateFee(session.getSpot().getSpotType(),session.calculateDurationMinutes()));
    }
}