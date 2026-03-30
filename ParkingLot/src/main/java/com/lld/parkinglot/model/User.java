package com.lld.parkinglot.model;

import com.lld.parkinglot.enums.VehicleType;
import com.lld.parkinglot.model.factory.Vehicle;
import com.lld.parkinglot.model.factory.VehicleFactory;
import com.lld.parkinglot.strategy.PaymentStrategy;

public class User {
    private final Vehicle vehicle;
    private final PaymentStrategy paymentStrategy;
    public void pay(double amount){
        paymentStrategy.processPayment(amount);
    }
    public User(VehicleType type, String vehicleNumber, PaymentStrategy paymentStrategy) {
        this.vehicle = VehicleFactory.create(type,vehicleNumber);
        this.paymentStrategy = paymentStrategy;
    }
    public Vehicle getVehicle(){
        return this.vehicle;
    }
}
