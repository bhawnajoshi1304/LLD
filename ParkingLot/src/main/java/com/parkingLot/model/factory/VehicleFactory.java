package main.java.com.parkingLot.model.factory;

import main.java.com.parkingLot.enums.VehicleType;
import main.java.com.parkingLot.model.User;

public class VehicleFactory {
    public static Vehicle createVehicle(VehicleType type, String licenceNumber, User user) {
        switch(type){
            case MOTORBIKE -> {
                return new Vehicle(licenceNumber,VehicleType.MOTORBIKE,user);
            }
            case CAR -> {
                return new Vehicle(licenceNumber, VehicleType.CAR,user);
            }
            case TRUCK -> {
                return  new Vehicle(licenceNumber, VehicleType.TRUCK,user);
            }
            default -> throw new IllegalArgumentException("No such vehicle type exist");
        }
    }


}
