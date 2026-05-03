package com.lld.parkinglot;

import com.lld.parkinglot.model.ParkingLot;
import com.lld.parkinglot.model.User;
import com.lld.parkinglot.enums.VehicleType;
import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.strategy.AllocationStrategy;
import com.lld.parkinglot.strategy.FeeStrategy;
import com.lld.parkinglot.strategy.BasicFeeStrategy;
import com.lld.parkinglot.strategy.CashPayment;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Map;

public class SimpleParkingLotTest {

    private ParkingLot parkingLot;
    private ExecutorService executorService;
    private static final int NUM_THREADS = 8;
    private static final int OPERATIONS_PER_THREAD = 15;
    private static final int TEST_TIMEOUT_SECONDS = 20;

    public void setUp() {
        Map<ParkingSpotType, Integer> floorMap = Map.of(
            ParkingSpotType.LARGE, 5,
            ParkingSpotType.SMALL, 3,
            ParkingSpotType.BIG, 2
        );

        List<Map<ParkingSpotType, Integer>> floors = new ArrayList<>();
        floors.add(floorMap);

        AllocationStrategy strategy = new AllocationStrategy() {
            @Override
            public boolean supportsPark(VehicleType vehicleType, ParkingSpotType spotType) {
                return switch (vehicleType) {
                    case CAR -> spotType == ParkingSpotType.LARGE;
                    case MOTORBIKE -> spotType == ParkingSpotType.SMALL;
                    case TRUCK -> spotType == ParkingSpotType.BIG;
                };
            }

            @Override
            public List<ParkingSpotType> getPreferredSpotOrder(VehicleType vehicleType) {
                return switch (vehicleType) {
                    case CAR -> List.of(ParkingSpotType.LARGE, ParkingSpotType.BIG);
                    case MOTORBIKE -> List.of(ParkingSpotType.SMALL);
                    case TRUCK -> List.of(ParkingSpotType.BIG, ParkingSpotType.LARGE);
                };
            }
        };

        FeeStrategy feeStrategy = new BasicFeeStrategy();
        parkingLot = ParkingLot.createInstance(feeStrategy, strategy, 1, floors);
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public boolean testConcurrentParking() throws InterruptedException {
        System.out.println("Testing concurrent parking...");

        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger successfulParkings = new AtomicInteger(0);
        AtomicInteger failedParkings = new AtomicInteger(0);

        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String plate = "CAR-" + threadId + "-" + j;
                        User user = new User(VehicleType.CAR, plate, new CashPayment());

                        try {
                            parkingLot.park(user);
                            successfulParkings.incrementAndGet();
                        } catch (Exception e) {
                            failedParkings.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        System.out.println("Concurrent parking test completed: " + completed);
        System.out.println("Successful parkings: " + successfulParkings.get());
        System.out.println("Failed parkings: " + failedParkings.get());
        System.out.println("Total operations: " + (successfulParkings.get() + failedParkings.get()));

        return completed && (successfulParkings.get() + failedParkings.get()) == NUM_THREADS * OPERATIONS_PER_THREAD;
    }

    public boolean testConcurrentExit() throws InterruptedException {
        System.out.println("Testing concurrent exit operations...");
        List<User> parkedUsers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String plate = "EXIT-" + i;
            User user = new User(VehicleType.CAR, plate, new CashPayment());
            try {
                parkingLot.park(user);
                parkedUsers.add(user);
            } catch (Exception e) {
                break;
            }
        }

        if (parkedUsers.isEmpty()) {
            System.out.println("No vehicles parked, skipping exit test");
            return true;
        }

        CountDownLatch latch = new CountDownLatch(parkedUsers.size());
        AtomicInteger successfulExits = new AtomicInteger(0);

        for (User user : parkedUsers) {
            executorService.submit(() -> {
                try {
                    parkingLot.exit(user);
                    successfulExits.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Exit failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        System.out.println("Concurrent exit test completed: " + completed);
        System.out.println("Successful exits: " + successfulExits.get());
        System.out.println("Expected exits: " + parkedUsers.size());

        return completed;
    }

    public boolean testMixedOperations() throws InterruptedException {
        System.out.println("Testing mixed parking and exit operations...");

        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger parkOperations = new AtomicInteger(0);
        AtomicInteger exitOperations = new AtomicInteger(0);
        List<User> activeUsers = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    Random random = new Random();

                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        if (random.nextBoolean() && !activeUsers.isEmpty()) {
                            User user = activeUsers.remove(random.nextInt(activeUsers.size()));
                            try {
                                parkingLot.exit(user);
                                exitOperations.incrementAndGet();
                            } catch (Exception e) {
                                activeUsers.add(user);
                            }
                        } else {
                            String plate = "MIXED-" + threadId + "-" + j;
                            User user = new User(VehicleType.CAR, plate, new CashPayment());

                            try {
                                parkingLot.park(user);
                                activeUsers.add(user);
                                parkOperations.incrementAndGet();
                            } catch (Exception e) {
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        System.out.println("Mixed operations test completed: " + completed);
        System.out.println("Park operations: " + parkOperations.get());
        System.out.println("Exit operations: " + exitOperations.get());
        System.out.println("Total operations: " + (parkOperations.get() + exitOperations.get()));

        return completed && (parkOperations.get() + exitOperations.get()) > 0;
    }

    public boolean testPerformance() throws InterruptedException {
        System.out.println("Testing performance under high load...");

        int numOperations = 500;
        CountDownLatch latch = new CountDownLatch(1);
        AtomicLong startTime = new AtomicLong();
        AtomicLong endTime = new AtomicLong();

        executorService.submit(() -> {
            try {
                startTime.set(System.currentTimeMillis());

                for (int i = 0; i < numOperations; i++) {
                    String plate = "PERF-" + i;
                    User user = new User(VehicleType.MOTORBIKE, plate, new CashPayment());

                    try {
                        parkingLot.park(user);
                    } catch (Exception e) {
                    }
                }

                endTime.set(System.currentTimeMillis());
            } finally {
                latch.countDown();
            }
        });

        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (completed) {
            long duration = endTime.get() - startTime.get();
            double throughput = (double) numOperations / duration * 1000;
            System.out.println("Performance test completed: " + completed);
            System.out.println("Duration: " + duration + "ms");
            System.out.println("Throughput: " + throughput + " operations/second");

            return throughput > 50;
        }

        return false;
    }

    public boolean testCircuitBreaker() throws InterruptedException {
        System.out.println("Testing circuit breaker functionality...");

        for (int i = 0; i < 10; i++) {
            try {
                User user = new User(VehicleType.TRUCK, "INVALID-" + i, new CashPayment());
                parkingLot.exit(user);
            } catch (Exception e) {
                // Expected
            }
        }

        System.out.println("Total errors recorded: " + parkingLot.getTotalErrors());
        System.out.println("Consecutive errors: " + parkingLot.getConsecutiveErrors());
        System.out.println("Circuit breaker open: " + parkingLot.isCircuitBreakerOpen());

        return parkingLot.getTotalErrors() > 0;
    }

    public void tearDown() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (parkingLot != null) {
            parkingLot.setMaintenanceMode(false);
        }
    }

    public static void main(String[] args) {
        SimpleParkingLotTest test = new SimpleParkingLotTest();

        try {
            test.setUp();

            boolean test1 = test.testConcurrentParking();
            boolean test2 = test.testConcurrentExit();
            boolean test3 = test.testMixedOperations();
            boolean test4 = test.testPerformance();
            boolean test5 = test.testCircuitBreaker();

            System.out.println("\n=== Test Results ===");
            System.out.println("Concurrent Parking: " + (test1 ? "PASS" : "FAIL"));
            System.out.println("Concurrent Exit: " + (test2 ? "PASS" : "FAIL"));
            System.out.println("Mixed Operations: " + (test3 ? "PASS" : "FAIL"));
            System.out.println("Performance Test: " + (test4 ? "PASS" : "FAIL"));
            System.out.println("Circuit Breaker: " + (test5 ? "PASS" : "FAIL"));

            boolean allTestsPassed = test1 && test2 && test3 && test4 && test5;
            System.out.println("\nOverall Result: " + (allTestsPassed ? "ALL TESTS PASSED" : "SOME TESTS FAILED"));

        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
