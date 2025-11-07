package main.java.com.parkingLot.strategy;

public class CashPayment implements PaymentStrategy {
    @Override
    public void processPayment(double amount) {
        System.out.println("Payment done through cash of "+amount);
    }
}