package com.lld.parkinglot.model.factory;

import com.lld.parkinglot.model.ParkingSession;
import com.lld.parkinglot.observer.ParkingObserver;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLotManager {
    private final List<ParkingObserver> exitObservers;
    private final List<ParkingObserver> entryObservers;
    private final ExecutorService notificationExecutor;
    private final ReentrantReadWriteLock observerLock;
    private final AtomicInteger notificationCounter;
    private final Map<String, Integer> notificationCounts;
    private static final Logger logger = Logger.getLogger(ParkingLotManager.class.getName());
    
    public ParkingLotManager() {
        this.entryObservers = new CopyOnWriteArrayList<>();
        this.exitObservers = new CopyOnWriteArrayList<>();
        this.observerLock = new ReentrantReadWriteLock(true);
        this.notificationCounter = new AtomicInteger(0);
        this.notificationCounts = new ConcurrentHashMap<>();
        
        this.notificationExecutor = Executors.newFixedThreadPool(
            4,
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "ParkingLotManager-Notification-" + 
                        threadNumber.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            }
        );
        
        logger.info("ParkingLotManager initialized with thread-safe observers");
    }
    
    public void registerExit(ParkingObserver obs){
        if(obs != null) {
            try {
                observerLock.writeLock().lock();
                if (!exitObservers.contains(obs)) {
                    exitObservers.add(obs);
                    logger.info("Registered exit observer: " + obs.getClass().getSimpleName());
                }
            } finally {
                observerLock.writeLock().unlock();
            }
        }
    }
    
    public void registerEntry(ParkingObserver obs){
        if(obs != null) {
            try {
                observerLock.writeLock().lock();
                if (!entryObservers.contains(obs)) {
                    entryObservers.add(obs);
                    logger.info("Registered entry observer: " + obs.getClass().getSimpleName());
                }
            } finally {
                observerLock.writeLock().unlock();
            }
        }
    }
    
    public void unregisterExit(ParkingObserver obs){
        if(obs != null) {
            try {
                observerLock.writeLock().lock();
                if (exitObservers.remove(obs)) {
                    logger.info("Unregistered exit observer: " + obs.getClass().getSimpleName());
                }
            } finally {
                observerLock.writeLock().unlock();
            }
        }
    }
    
    public void unregisterEntry(ParkingObserver obs){
        if(obs != null) {
            try {
                observerLock.writeLock().lock();
                if (entryObservers.remove(obs)) {
                    logger.info("Unregistered entry observer: " + obs.getClass().getSimpleName());
                }
            } finally {
                observerLock.writeLock().unlock();
            }
        }
    }
    
    public void notifyExit(ParkingSession parkingSession){
        if (parkingSession == null) {
            logger.warning("Null parking session provided for exit notification");
            return;
        }
        
        int notificationId = notificationCounter.incrementAndGet();
        logger.fine("Starting exit notification " + notificationId + 
            " for vehicle " + parkingSession.getUser().getVehicle().getNumberPlate());

        notificationExecutor.submit(() -> {
            List<ParkingObserver> observersCopy;
            try {
                observerLock.readLock().lock();
                observersCopy = List.copyOf(exitObservers);
            } finally {
                observerLock.readLock().unlock();
            }
            
            for (ParkingObserver obs : observersCopy) {
                try {
                    obs.onVehicleExit(parkingSession);
                    notificationCounts.merge(obs.getClass().getSimpleName(), 1, Integer::sum);
                } catch (Exception e) {
                    logger.log(Level.WARNING, 
                        "Error notifying exit observer " + obs.getClass().getSimpleName(), e);
                }
            }
            
            logger.fine("Completed exit notification " + notificationId);
        });
    }
    
    public void notifyEntry(ParkingSession parkingSession){
        if (parkingSession == null) {
            logger.warning("Null parking session provided for entry notification");
            return;
        }
        
        int notificationId = notificationCounter.incrementAndGet();
        logger.fine("Starting entry notification " + notificationId + 
            " for vehicle " + parkingSession.getUser().getVehicle().getNumberPlate());

        notificationExecutor.submit(() -> {
            List<ParkingObserver> observersCopy;
            try {
                observerLock.readLock().lock();
                observersCopy = List.copyOf(entryObservers);
            } finally {
                observerLock.readLock().unlock();
            }
            
            for (ParkingObserver obs : observersCopy) {
                try {
                    obs.onVehicleEntry(parkingSession);
                    notificationCounts.merge(obs.getClass().getSimpleName(), 1, Integer::sum);
                } catch (Exception e) {
                    logger.log(Level.WARNING, 
                        "Error notifying entry observer " + obs.getClass().getSimpleName(), e);
                }
            }
            
            logger.fine("Completed entry notification " + notificationId);
        });
    }

    public int getEntryObserverCount() {
        try {
            observerLock.readLock().lock();
            return entryObservers.size();
        } finally {
            observerLock.readLock().unlock();
        }
    }
    
    public int getExitObserverCount() {
        try {
            observerLock.readLock().lock();
            return exitObservers.size();
        } finally {
            observerLock.readLock().unlock();
        }
    }

    public Map<String, Integer> getNotificationStatistics() {
        return Map.copyOf(notificationCounts);
    }

    public void resetNotificationStatistics() {
        notificationCounts.clear();
        logger.info("Notification statistics reset");
    }

    public void shutdown() {
        logger.info("Shutting down ParkingLotManager");
        notificationExecutor.shutdown();
        try {
            if (!notificationExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                notificationExecutor.shutdownNow();
                if (!notificationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warning("Notification executor did not terminate gracefully");
                }
            }
        } catch (InterruptedException e) {
            notificationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("ParkingLotManager shutdown complete");
    }

    public boolean isShutdown() {
        return notificationExecutor.isShutdown();
    }
}