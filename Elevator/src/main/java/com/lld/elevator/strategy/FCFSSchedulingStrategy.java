package com.lld.elevator.strategy;

import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.ElevatorRequest;
import com.lld.elevator.model.ExternalRequest;
import com.lld.elevator.model.InternalRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Comparator;
import java.util.logging.Logger;

public class FCFSSchedulingStrategy implements SchedulingStrategy{
    private final BlockingQueue<ElevatorRequest> requests;
    private final AtomicInteger requestCounter;
    private final ReentrantReadWriteLock lock;
    private static final Logger logger = Logger.getLogger(FCFSSchedulingStrategy.class.getName());
    
    public FCFSSchedulingStrategy() {
        this.requests = new PriorityBlockingQueue<>(100, 
            Comparator.comparingLong(ElevatorRequest::getTimestamp));
        this.requestCounter = new AtomicInteger(0);
        this.lock = new ReentrantReadWriteLock();
    }
    
    @Override
    public void addRequest(ElevatorRequest elevatorRequest, Elevator elevator) {
        try {
            lock.writeLock().lock();
            ElevatorRequest requestWithTimestamp = elevatorRequest;
            if (elevatorRequest.getTimestamp() == 0) {
                if (elevatorRequest instanceof ExternalRequest) {
                    ExternalRequest extReq = (ExternalRequest) elevatorRequest;
                    requestWithTimestamp = new ExternalRequest(extReq.floor(), extReq.direction(), System.currentTimeMillis());
                } else if (elevatorRequest instanceof InternalRequest) {
                    InternalRequest intReq = (InternalRequest) elevatorRequest;
                    requestWithTimestamp = new InternalRequest(intReq.floor(), System.currentTimeMillis());
                }
            }
            
            if (requests.offer(requestWithTimestamp)) {
                logger.info("FCFS: Added request for floor " + requestWithTimestamp.getFloor() + 
                    " at timestamp " + requestWithTimestamp.getTimestamp());
                requestCounter.incrementAndGet();
            } else {
                logger.warning("FCFS: Failed to add request for floor " + requestWithTimestamp.getFloor());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int getNextStop(Elevator elevator) {
        try {
            lock.readLock().lock();
            ElevatorRequest nextRequest = requests.peek();
            if (nextRequest == null) {
                return elevator.getCurrentFloor();
            }
            return nextRequest.getFloor();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removeReachedFloor(int floor) {
        try {
            lock.writeLock().lock();
            ElevatorRequest nextRequest = requests.peek();
            if (nextRequest != null && nextRequest.getFloor() == floor) {
                ElevatorRequest removed = requests.poll();
                if (removed != null) {
                    logger.info("FCFS: Removed request for floor " + floor + 
                        " (timestamp: " + removed.getTimestamp() + ")");
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int getNextFloor(Elevator elevator) {
        try {
            lock.readLock().lock();
            ElevatorRequest nextRequest = requests.peek();
            if (nextRequest == null) {
                return elevator.getCurrentFloor();
            }
            
            int nextRequestFloor = nextRequest.getFloor();
            int currentFloor = elevator.getCurrentFloor();
            
            logger.fine("FCFS: Next floor is " + nextRequestFloor + " from current floor " + currentFloor);
            return nextRequestFloor;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean hasRequests() {
        return !requests.isEmpty();
    }
    
    public int getRequestCount() {
        return requests.size();
    }
    
    public void clearRequests() {
        try {
            lock.writeLock().lock();
            int clearedCount = requests.size();
            requests.clear();
            logger.info("FCFS: Cleared " + clearedCount + " pending requests");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public int getTotalRequestsProcessed() {
        return requestCounter.get();
    }
}
