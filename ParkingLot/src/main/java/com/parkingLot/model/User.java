package main.java.com.parkingLot.model;

import main.java.com.parkingLot.enums.VehicleType;
import main.java.com.parkingLot.model.factory.Vehicle;
import main.java.com.parkingLot.model.factory.VehicleFactory;
import main.java.com.parkingLot.strategy.PaymentStrategy;

public class User {
    private final Vehicle vehicle;
    private final PaymentStrategy paymentStrategy;
    public void pay(double amount){
        paymentStrategy.processPayment(amount);
    }
    public User(VehicleType type, String vehicleNumber, PaymentStrategy paymentStrategy) {
        this.vehicle = VehicleFactory.createVehicle(type,vehicleNumber,this);
        this.paymentStrategy = paymentStrategy;
    }
    public Vehicle getVehicle(){
        return this.vehicle;
    }
}
