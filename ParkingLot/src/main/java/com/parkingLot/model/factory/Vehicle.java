package main.java.com.parkingLot.model.factory;

import main.java.com.parkingLot.enums.VehicleType;

public class Vehicle {
    private final String numberPlate;
    private final VehicleType type;
    Vehicle(String numberPlate, VehicleType type){
        this.numberPlate = numberPlate;
        this.type = type;
    }
    public VehicleType getVehicleType() {
        return type;
    }
    public String getNumberPlate(){
        return numberPlate;
    }
}