package com.lld.parkinglot.model.factory;

import com.lld.parkinglot.enums.VehicleType;

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