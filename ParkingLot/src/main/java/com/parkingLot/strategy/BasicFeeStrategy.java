package main.java.com.parkingLot.strategy;

import main.java.com.parkingLot.enums.ParkingSpotType;

public class BasicFeeStrategy implements FeeStrategy {
    @Override
    public double calculateFee(ParkingSpotType type, long duration) {
        double fee = 30;
        switch(type){
            case SMALL -> fee+=20*duration;
            case BIG -> fee+=30*duration+10;
            case LARGE -> fee+=40*duration+20;
        }
        return fee;
    }
}