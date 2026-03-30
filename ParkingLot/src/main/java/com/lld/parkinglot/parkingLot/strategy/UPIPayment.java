package main.java.com.lld.parkinglot.strategy;

public class UPIPayment implements PaymentStrategy {
    @Override
    public void processPayment(double amount) {
        System.out.println("Payment done through cash of "+amount);
    }
}