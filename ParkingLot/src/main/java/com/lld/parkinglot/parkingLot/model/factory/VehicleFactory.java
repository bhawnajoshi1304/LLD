package main.java.com.lld.parkinglot.model.factory;

import main.java.com.lld.parkinglot.enums.VehicleType;

public class VehicleFactory {
    public static Vehicle create(VehicleType type, String licenceNumber) {
        switch(type){
            case MOTORBIKE -> {
                return new Vehicle(licenceNumber,VehicleType.MOTORBIKE);
            }
            case CAR -> {
                return new Vehicle(licenceNumber, VehicleType.CAR);
            }
            case TRUCK -> {
                return  new Vehicle(licenceNumber, VehicleType.TRUCK);
            }
            default -> throw new IllegalArgumentException("No such vehicle type exist");
        }
    }


}
