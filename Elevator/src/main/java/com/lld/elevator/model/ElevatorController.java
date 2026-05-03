package com.lld.elevator.model;

import com.lld.elevator.observer.ElevatorObserver;
import com.lld.elevator.strategy.SchedulingStrategy;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ElevatorController {
    @Getter
    private final List<Elevator> elevators;
    private final SchedulingStrategy strategy;
    private final ExecutorService elevatorExecutorService;
    private final BlockingQueue<ElevatorRequest> requestQueue;
    private final Thread requestDispatcherThread;
    private volatile boolean running;
    private final AtomicInteger requestCounter;
    private static final Logger logger = Logger.getLogger(ElevatorController.class.getName());
    
    private final Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.log(Level.SEVERE, "Uncaught exception in thread " + t.getName(), e);
            handleThreadFailure(t, e);
        }
    };
    
    public ElevatorController(List<Elevator> elevators, SchedulingStrategy strategy) {
        this.elevators = elevators;
        this.strategy = strategy;
        this.requestQueue = new LinkedBlockingQueue<>();
        this.requestCounter = new AtomicInteger(0);
        this.running = true;
        
        this.elevatorExecutorService = Executors.newFixedThreadPool(
            elevators.size(),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "ElevatorController-Worker-" + threadNumber.getAndIncrement());
                    t.setDaemon(false);
                    t.setUncaughtExceptionHandler(exceptionHandler);
                    return t;
                }
            }
        );
        
        this.requestDispatcherThread = new Thread(this::requestDispatcherLoop, "RequestDispatcher");
        this.requestDispatcherThread.setDaemon(false);
        this.requestDispatcherThread.setUncaughtExceptionHandler(exceptionHandler);
        
        startElevators();
        requestDispatcherThread.start();
        
        logger.info("ElevatorController initialized with " + elevators.size() + " elevators");
    }
    
    public void step() {
        for (Elevator elevator : elevators) {
            if (!elevator.isRunning()) {
                logger.warning("Elevator " + elevator.getId() + " is not running, attempting restart");
                elevator.restart();
            }
        }
    }
    
    private void startElevators() {
        for (Elevator elevator : elevators) {
            elevator.start();
        }
    }
    
    private void requestDispatcherLoop() {
        logger.info("Request dispatcher thread started");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                ElevatorRequest request = requestQueue.take();
                dispatchRequest(request);
            } catch (InterruptedException e) {
                logger.info("Request dispatcher thread interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in request dispatcher loop", e);
            }
        }
        logger.info("Request dispatcher thread stopped");
    }
    
    private void dispatchRequest(ElevatorRequest request) {
        try {
            Elevator optimalElevator = findOptimalElevator(request);
            if (optimalElevator != null) {
                optimalElevator.addRequest(request);
                logger.info("Dispatched request for floor " + request.getFloor() + 
                    " to elevator " + optimalElevator.getId());
            } else {
                logger.warning("No optimal elevator found for floor " + request.getFloor() + 
                    ", falling back to round-robin");
                addRequestRoundRobin(request);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error dispatching request for floor " + request.getFloor(), e);
        }
    }
    
    private Elevator findOptimalElevator(ElevatorRequest request) {
        return elevators.stream()
            .filter(elevator -> elevator.isRunning())
            .min((e1, e2) -> Integer.compare(
                calculateDistance(e1, request.getFloor()),
                calculateDistance(e2, request.getFloor())
            ))
            .orElse(null);
    }
    
    private int calculateDistance(Elevator elevator, int targetFloor) {
        return Math.abs(elevator.getCurrentFloor() - targetFloor);
    }
    
    private void addRequestRoundRobin(ElevatorRequest request) {
        int requestId = requestCounter.getAndIncrement();
        Elevator elevator = elevators.get(requestId % elevators.size());
        elevator.addRequest(request);
    }
    
    public void addRequest(ElevatorRequest request) {
        try {
            if (requestQueue.offer(request)) {
                logger.info("Request queued for floor " + request.getFloor());
            } else {
                logger.warning("Request queue full for floor " + request.getFloor());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding request to queue", e);
        }
    }
    
    public void addRequestToSpecificElevator(int elevatorId, ElevatorRequest request) {
        if (elevatorId >= 0 && elevatorId < elevators.size()) {
            Elevator elevator = elevators.get(elevatorId);
            if (elevator.isRunning()) {
                elevator.addRequest(request);
                logger.info("Direct request to elevator " + elevatorId + " for floor " + request.getFloor());
            } else {
                logger.warning("Elevator " + elevatorId + " is not running");
            }
        } else {
            logger.warning("Invalid elevator ID: " + elevatorId);
        }
    }
    
    public void addObserver(ElevatorObserver observer) {
        for (Elevator elevator : elevators) {
            elevator.addStateObserver(observer);
            elevator.addFloorObserver(observer);
        }
    }
    
    public boolean areAllElevatorsIdle() {
        return elevators.stream()
            .allMatch(e -> !e.hasPendingRequests() && 
                         e.getState().getStateName().equals("IDLE"));
    }
    
    public void shutdown() {
        logger.info("Shutting down ElevatorController");
        running = false;
        
        requestDispatcherThread.interrupt();
        try {
            requestDispatcherThread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        for (Elevator elevator : elevators) {
            elevator.stop();
        }
        
        elevatorExecutorService.shutdown();
        try {
            if (!elevatorExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                elevatorExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            elevatorExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("ElevatorController shutdown complete");
    }
    
    private void handleThreadFailure(Thread failedThread, Throwable throwable) {
        logger.severe("Handling thread failure for " + failedThread.getName());
        
        if (failedThread.getName().startsWith("Elevator-")) {
            elevators.stream()
                .filter(e -> !e.isRunning())
                .forEach(e -> {
                    logger.info("Attempting to restart elevator " + e.getId());
                    e.restart();
                });
        }
    }
    
    public int getPendingRequestCount() {
        return requestQueue.size();
    }
    
    public int getActiveElevatorCount() {
        return (int) elevators.stream().filter(Elevator::isRunning).count();
    }

}
