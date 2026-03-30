package main.java.com.lld.parkinglot.strategy;

public class CardPayment implements PaymentStrategy {
    @Override
    public void processPayment(double amount) {
        System.out.println("Payment done through card of "+amount);
    }
}
