package com.lld.parkinglot.strategy;

import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.model.ParkingSession;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;

public class ThreadSafePaymentStrategy implements FeeStrategy {
    
    private final Map<ParkingSpotType, Double> hourlyRates;
    private final Map<String, PaymentTransaction> transactionHistory;
    private final ReadWriteLock rateLock;
    private final AtomicInteger transactionCounter;
    private final AtomicLong totalRevenue;
    private static final int GRACE_PERIOD_MINUTES = 15;
    private static final double MAX_DAILY_CHARGE = 50.0;
    
    public ThreadSafePaymentStrategy() {
        this.hourlyRates = new ConcurrentHashMap<>();
        this.transactionHistory = new ConcurrentHashMap<>();
        this.rateLock = new ReentrantReadWriteLock(true);
        this.transactionCounter = new AtomicInteger(0);
        this.totalRevenue = new AtomicLong(0);
        initializeDefaultRates();
    }
    private void initializeDefaultRates() {
        try {
            rateLock.writeLock().lock();
            hourlyRates.put(ParkingSpotType.SMALL, 2.0);
            hourlyRates.put(ParkingSpotType.BIG, 5.0);
            hourlyRates.put(ParkingSpotType.LARGE, 10.0);
            
        } finally {
            rateLock.writeLock().unlock();
        }
    }
    
        @Override
    public double calculateFee(ParkingSpotType type, long durationMinutes) {
        try {
            rateLock.readLock().lock();
            
            Double hourlyRate = hourlyRates.get(type);
            if (hourlyRate == null) {
                return 0.0;
            }
            if (durationMinutes <= GRACE_PERIOD_MINUTES) {
                return 0.0;
            }
            long billableHours = (durationMinutes + 59) / 60;
            double baseFee = hourlyRate * billableHours;
            double finalFee = Math.min(baseFee, MAX_DAILY_CHARGE);
            
            return finalFee;
        } finally {
            rateLock.readLock().unlock();
        }
    }
    
        public double calculateSessionFee(ParkingSession session) {
        if (session == null || session.getStartTime() == null) {
            return 0.0;
        }

        LocalDateTime endTime = session.getEndTime() != null ? 
            session.getEndTime() : LocalDateTime.now();
        long durationMinutes = ChronoUnit.MINUTES.between(
            session.getStartTime(), endTime);
        double fee = calculateFee(session.getSpot().getSpotType(), durationMinutes);
        recordTransaction(session, fee, durationMinutes);
        
        return fee;
    }
    
        public void updateHourlyRate(ParkingSpotType spotType, double newRate) {
        try {
            rateLock.writeLock().lock();
            hourlyRates.put(spotType, newRate);
        } finally {
            rateLock.writeLock().unlock();
        }
    }
    
        public Map<ParkingSpotType, Double> getCurrentRates() {
        try {
            rateLock.readLock().lock();
            return Map.copyOf(hourlyRates);
        } finally {
            rateLock.readLock().unlock();
        }
    }
    
        private void recordTransaction(ParkingSession session, double amount, long durationMinutes) {
            String transactionId = "TXN_" + transactionCounter.incrementAndGet();
            PaymentTransaction transaction = new PaymentTransaction(
                    transactionId,
                    session.getUser().getVehicle().getNumberPlate(),
                    session.getSpot().getSpotId(),
                    amount,
                    durationMinutes,
                    LocalDateTime.now()
            );
            transactionHistory.put(transactionId, transaction);
            totalRevenue.addAndGet((long) (amount * 100));
        }
    
        public Map<String, PaymentTransaction> getTransactionHistory() {
        return Map.copyOf(transactionHistory);
    }
    
        public double getTotalRevenue() {
        return totalRevenue.get() / 100.0;
    }
    
        public Map<ParkingSpotType, Double> getRevenueBySpotType() {
        Map<ParkingSpotType, Double> revenueByType = new ConcurrentHashMap<>();
        
        transactionHistory.values().forEach(transaction -> {
            ParkingSpotType spotType = ParkingSpotType.valueOf(
                transaction.getSpotId().split("_")[1]);
            revenueByType.merge(spotType, transaction.getAmount(), Double::sum);
        });
        
        return revenueByType;
    }
    
        public void clearTransactionHistory() {
        transactionHistory.clear();
        totalRevenue.set(0);
    }
    
        public TransactionStatistics getTransactionStatistics() {
        int totalTransactions = transactionHistory.size();
        double totalRevenueAmount = getTotalRevenue();
        double averageTransactionAmount = totalTransactions > 0 ? 
            totalRevenueAmount / totalTransactions : 0.0;
        
        return new TransactionStatistics(
            totalTransactions,
            totalRevenueAmount,
            averageTransactionAmount,
            getRevenueBySpotType()
        );
    }
    
    public static class PaymentTransaction {
        private final String transactionId;
        private final String vehicleNumber;
        private final String spotId;
        private final double amount;
        private final long durationMinutes;
        private final LocalDateTime timestamp;
        public PaymentTransaction(String transactionId, String vehicleNumber, 
                               String spotId, double amount, long durationMinutes,
                               LocalDateTime timestamp) {
            this.transactionId = transactionId;
            this.vehicleNumber = vehicleNumber;
            this.spotId = spotId;
            this.amount = amount;
            this.durationMinutes = durationMinutes;
            this.timestamp = timestamp;
        }
        
        public String getTransactionId() { return transactionId; }
        public String getVehicleNumber() { return vehicleNumber; }
        public String getSpotId() { return spotId; }
        public double getAmount() { return amount; }
        public long getDurationMinutes() { return durationMinutes; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public static class TransactionStatistics {
        private final int totalTransactions;
        private final double totalRevenue;
        private final double averageTransactionAmount;
        private final Map<ParkingSpotType, Double> revenueBySpotType;
        public TransactionStatistics(int totalTransactions, double totalRevenue,
                                 double averageTransactionAmount,
                                 Map<ParkingSpotType, Double> revenueBySpotType) {
            this.totalTransactions = totalTransactions;
            this.totalRevenue = totalRevenue;
            this.averageTransactionAmount = averageTransactionAmount;
            this.revenueBySpotType = revenueBySpotType;
        }
        
        public int getTotalTransactions() { return totalTransactions; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAverageTransactionAmount() { return averageTransactionAmount; }
        public Map<ParkingSpotType, Double> getRevenueBySpotType() { return revenueBySpotType; }
    }
}
