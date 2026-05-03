package com.lld.elevator.model;

import com.lld.elevator.observer.ElevatorObserver;
import com.lld.elevator.state.ElevatorState;
import com.lld.elevator.state.IdleState;
import com.lld.elevator.strategy.SchedulingStrategy;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import java.util.logging.Level;

@Getter
public class Elevator implements Runnable {
    private final int id;
    private final int minFloor;
    private final int maxFloor;
    private final AtomicInteger currentFloor;
    private final AtomicReference<ElevatorState> state;
    private final SchedulingStrategy strategy;
    private final List<ElevatorObserver> stateObservers;
    private final List<ElevatorObserver> floorObservers;
    private final BlockingQueue<ElevatorRequest> requestQueue;
    private volatile boolean running;
    private final ExecutorService executorService;
    private static final Logger logger = Logger.getLogger(Elevator.class.getName());
    private static final int STEP_DELAY_MS = 1000;
    
    public Elevator(int id, int noOfFloors, SchedulingStrategy strategy) {
        this.id = id;
        this.currentFloor = new AtomicInteger(0);
        this.minFloor = 0;
        this.maxFloor = noOfFloors - 1;
        this.state = new AtomicReference<>(IdleState.INSTANCE);
        this.strategy = strategy;
        this.stateObservers = new ArrayList<>();
        this.floorObservers = new ArrayList<>();
        this.requestQueue = new LinkedBlockingQueue<>();
        this.running = true;
        this.executorService = Executors.newSingleThreadExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "Elevator-" + id);
                    t.setDaemon(false);
                    return t;
                }
            }
        );
        logger.info("Elevator " + id + " initialized at floor " + currentFloor.get());
    }
    
    public int getId() {
        return id;
    }
    
    public int getMinFloor() {
        return minFloor;
    }
    
    public int getMaxFloor() {
        return maxFloor;
    }
    
    public SchedulingStrategy getStrategy() {
        return strategy;
    }
    
    public void addRequest(ElevatorRequest request) {
        int floor = request.getFloor();
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException("Invalid floor request: " + floor + 
                ". Valid range: " + minFloor + "-" + maxFloor);
        }
        try {
            if (requestQueue.offer(request)) {
                logger.info("Elevator " + id + " received request for floor " + floor);
                synchronized (this) {
                    notifyAll();
                }
            } else {
                logger.warning("Elevator " + id + " request queue full for floor " + floor);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding request to elevator " + id, e);
        }
    }
    
    public void step() {
        try {
            ElevatorState currentState = state.get();
            ElevatorState newState = currentState.step(this);
            
            if (newState != currentState) {
                if (state.compareAndSet(currentState, newState)) {
                    newState.onEnter(this);
                    logger.info("Elevator " + id + " state changed from " + 
                        currentState.getStateName() + " to " + newState.getStateName());
                    notifyStateObservers();
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during elevator " + id + " step execution", e);
        }
    }
    
    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty() || strategy.hasRequests();
    }
    
    public ElevatorRequest getNextRequest() throws InterruptedException {
        return requestQueue.take();
    }
    
    public boolean hasQueuedRequests() {
        return !requestQueue.isEmpty();
    }
    
    public int getCurrentFloor() {
        return currentFloor.get();
    }
    
    public void setCurrentFloor(int floor) {
        int oldFloor = currentFloor.getAndSet(floor);
        if (oldFloor != floor) {
            logger.info("Elevator " + id + " moved from floor " + oldFloor + " to " + floor);
            notifyFloorObservers();
        }
    }
    
    public ElevatorState getState() {
        return state.get();
    }
    
    public void setState(ElevatorState newState) {
        ElevatorState oldState = state.getAndSet(newState);
        if (oldState != newState) {
            logger.info("Elevator " + id + " state changed from " + 
                oldState.getStateName() + " to " + newState.getStateName());
            notifyStateObservers();
        }
    }
    
    public void addStateObserver(ElevatorObserver observer) {
        stateObservers.add(observer);
    }
    
    public void addFloorObserver(ElevatorObserver observer) {
        floorObservers.add(observer);
    }
    
    public void notifyFloorObservers() {
        for (ElevatorObserver observer : floorObservers) {
            try {
                observer.onElevatorFloorChanged(currentFloor.get());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error notifying floor observer for elevator " + id, e);
            }
        }
    }
    
    private void notifyStateObservers() {
        for (ElevatorObserver observer : stateObservers) {
            try {
                observer.onElevatorStateChanged(state.get());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error notifying state observer for elevator " + id, e);
            }
        }
    }
    
    @Override
    public void run() {
        logger.info("Elevator " + id + " thread started");
        int consecutiveErrors = 0;
        final int MAX_CONSECUTIVE_ERRORS = 3;
        final long ERROR_RETRY_DELAY_MS = 1000;
        
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                if (hasPendingRequests()) {
                    step();
                    Thread.sleep(STEP_DELAY_MS);
                    consecutiveErrors = 0;
                } else {
                    synchronized (this) {
                        if (!hasPendingRequests()) {
                            wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                logger.info("Elevator " + id + " thread interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                consecutiveErrors++;
                logger.log(Level.WARNING, "Error in elevator " + id + " (attempt " + consecutiveErrors + "/" + MAX_CONSECUTIVE_ERRORS + ")", e);
                
                if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                    logger.log(Level.SEVERE, "Elevator " + id + " exceeded max consecutive errors, entering recovery mode");
                    enterRecoveryMode();
                    consecutiveErrors = 0;
                } else {
                    try {
                        Thread.sleep(ERROR_RETRY_DELAY_MS * consecutiveErrors);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        logger.info("Elevator " + id + " thread stopped");
    }
    
    private void enterRecoveryMode() {
        logger.warning("Elevator " + id + " entering recovery mode");
        try {
            state.set(com.lld.elevator.state.IdleState.INSTANCE);
            currentFloor.set(0);
            
            while (requestQueue.poll() != null) {
            }
            
            notifyStateObservers();
            notifyFloorObservers();
            
            logger.info("Elevator " + id + " recovery completed");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Elevator " + id + " recovery failed", e);
        }
    }
    
    public void restart() {
        if (!running) {
            logger.info("Attempting to restart elevator " + id);
            running = true;
            Thread newThread = new Thread(this, "Elevator-" + id + "-Recovery");
            newThread.setUncaughtExceptionHandler((t, e) -> {
                logger.log(Level.SEVERE, "Uncaught exception in restarted elevator " + id, e);
                running = false;
            });
            newThread.start();
        }
    }
    
    public void start() {
        executorService.submit(this);
        logger.info("Elevator " + id + " started");
    }
    
    public void stop() {
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Elevator " + id + " stopped");
    }
    
    public boolean isRunning() {
        return running && !executorService.isShutdown();
    }
}
