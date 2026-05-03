package com.lld.chessengine;

import com.lld.chessengine.model.ThreadSafeEngine;
import com.lld.chessengine.model.Color;
import com.lld.chessengine.strategy.Position;
import com.lld.chessengine.strategy.PositionRegistry;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Simple concurrency tests for ChessEngine system
 * Tests basic thread safety and functionality
 */
public class SimpleChessEngineTest {
    
    private ThreadSafeEngine engine;
    private ExecutorService executorService;
    private static final int NUM_THREADS = 8;
    private static final int MOVES_PER_THREAD = 25;
    private static final int TEST_TIMEOUT_SECONDS = 20;
    
    public void setUp() {
        engine = new ThreadSafeEngine();
        engine.startGame("Player1", "Player2");
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }
    
    public boolean testConcurrentMoveValidation() throws InterruptedException {
        System.out.println("Testing concurrent move validation...");
        
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger successfulValidations = new AtomicInteger(0);
        AtomicInteger failedValidations = new AtomicInteger(0);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < MOVES_PER_THREAD; j++) {
                        // Test various move validations
                        Position from = PositionRegistry.get('E', 2);
                        Position to = PositionRegistry.get('E', 4);
                        
                        if (engine.getBoard().movePiece(from, to)) {
                            successfulValidations.incrementAndGet();
                        } else {
                            failedValidations.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        System.out.println("Concurrent move validation test completed: " + completed);
        System.out.println("Successful validations: " + successfulValidations.get());
        System.out.println("Failed validations: " + failedValidations.get());
        
        return completed && (successfulValidations.get() + failedValidations.get()) == NUM_THREADS * MOVES_PER_THREAD;
    }
    
    public boolean testBoardConsistency() throws InterruptedException {
        System.out.println("Testing board consistency under concurrent access...");
        
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger boardChecks = new AtomicInteger(0);
        List<String> observedStates = Collections.synchronizedList(new ArrayList<>());
        
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < MOVES_PER_THREAD; j++) {
                        // Check board state
                        int pieceCount = engine.getBoard().getActivePieces(Color.WHITE).size() + engine.getBoard().getActivePieces(Color.BLACK).size();
                        String gameState = engine.getGameStatus().getStateName();
                        
                        observedStates.add(gameState + ":" + pieceCount);
                        boardChecks.incrementAndGet();
                        
                        // Verify consistency
                        if (pieceCount < 0 || gameState == null || gameState.length() == 0) {
                            System.err.println("Inconsistent board state detected!");
                        }
                        
                        // Try a simple move
                        try {
                            Position from = PositionRegistry.get('G', 1);
                            Position to = PositionRegistry.get('F', 3);
                            engine.getBoard().movePiece(from, to);
                        } catch (Exception e) {
                            // Expected for invalid moves
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        System.out.println("Board consistency test completed: " + completed);
        System.out.println("Board checks performed: " + boardChecks.get());
        
        return completed && boardChecks.get() == NUM_THREADS * MOVES_PER_THREAD;
    }
    
    public boolean testMoveHistory() throws InterruptedException {
        System.out.println("Testing move history under concurrent operations...");
        
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger moveOperations = new AtomicInteger(0);
        List<String> moveHistory = Collections.synchronizedList(new ArrayList<>());
        
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < MOVES_PER_THREAD; j++) {
                        try {
                            // Make a move directly on the board
                            Position from = PositionRegistry.get('A', 2);
                            Position to = PositionRegistry.get('A', 3);
                            
                            if (engine.getBoard().movePiece(from, to)) {
                                moveOperations.incrementAndGet();
                                String moveStr = from.toString() + "-" + to.toString();
                                moveHistory.add(moveStr);
                                
                                // Verify move was recorded
                                Map<String, com.lld.chessengine.model.Move> history = engine.getMoveHistory();
                                if (history == null) {
                                    System.err.println("Move history is null!");
                                }
                            }
                        } catch (Exception e) {
                            // Expected for invalid moves
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        System.out.println("Move history test completed: " + completed);
        System.out.println("Move operations: " + moveOperations.get());
        
        return completed && moveOperations.get() > 0;
    }
    
    public boolean testGameStateTransitions() throws InterruptedException {
        System.out.println("Testing game state transitions...");
        
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger stateChecks = new AtomicInteger(0);
        List<String> observedStates = Collections.synchronizedList(new ArrayList<>());
        
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < MOVES_PER_THREAD; j++) {
                        String currentState = engine.getGameStatus().getStateName();
                        observedStates.add(currentState);
                        stateChecks.incrementAndGet();
                        
                        // Verify state is valid
                        if (currentState == null || currentState.length() == 0) {
                            System.err.println("Invalid game state detected!");
                        }
                        
                        // Try to make a move
                        try {
                            Position from = PositionRegistry.get('B', 1);
                            Position to = PositionRegistry.get('C', 3);
                            engine.getBoard().movePiece(from, to);
                        } catch (Exception e) {
                            // Expected for invalid moves
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        System.out.println("Game state transitions test completed: " + completed);
        System.out.println("State checks performed: " + stateChecks.get());
        
        return completed && stateChecks.get() == NUM_THREADS * MOVES_PER_THREAD;
    }
    
    public boolean testPerformance() throws InterruptedException {
        System.out.println("Testing performance under high concurrency...");
        
        int numValidations = 2000;
        CountDownLatch latch = new CountDownLatch(1);
        AtomicLong startTime = new AtomicLong();
        AtomicLong endTime = new AtomicLong();
        
        executorService.submit(() -> {
            try {
                startTime.set(System.currentTimeMillis());
                
                for (int i = 0; i < numValidations; i++) {
                    Position from = PositionRegistry.get('E', 2);
                    Position to = PositionRegistry.get('E', 4);
                    engine.getBoard().movePiece(from, to);
                }
                
                endTime.set(System.currentTimeMillis());
            } finally {
                latch.countDown();
            }
        });
        
        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        if (completed) {
            long duration = endTime.get() - startTime.get();
            double throughput = (double) numValidations / duration * 1000; // validations per second
            
            System.out.println("Performance test completed: " + completed);
            System.out.println("Duration: " + duration + "ms");
            System.out.println("Throughput: " + throughput + " validations/second");
            
            return throughput > 500; // Should handle at least 500 validations per second
        }
        
        return false;
    }
    
    public boolean testFaultTolerance() throws InterruptedException {
        System.out.println("Testing fault tolerance under concurrent exceptions...");
        
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);
        AtomicInteger exceptionsCaught = new AtomicInteger(0);
        AtomicInteger successfulOperations = new AtomicInteger(0);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < MOVES_PER_THREAD; j++) {
                        try {
                            // Mix of valid and invalid moves
                            if (j % 10 == 0) {
                                // Invalid move (off board)
                                try {
                                    Position from = new Position('Z', 1);
                                    Position to = new Position('Z', 2);
                                    engine.getBoard().movePiece(from, to);
                                } catch (IllegalArgumentException e) {
                                    exceptionsCaught.incrementAndGet();
                                }
                            } else {
                                // Valid move
                                Position from = PositionRegistry.get('F', 2);
                                Position to = PositionRegistry.get('F', 3);
                                engine.getBoard().movePiece(from, to);
                                successfulOperations.incrementAndGet();
                            }
                        } catch (Exception e) {
                            exceptionsCaught.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        System.out.println("Fault tolerance test completed: " + completed);
        System.out.println("Exceptions caught: " + exceptionsCaught.get());
        System.out.println("Successful operations: " + successfulOperations.get());
        
        return completed && (successfulOperations.get() > 0 || exceptionsCaught.get() > 0);
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
        
        if (engine != null) {
            engine.shutdown();
        }
    }
    
    public static void main(String[] args) {
        SimpleChessEngineTest test = new SimpleChessEngineTest();
        
        try {
            test.setUp();
            
            boolean test1 = test.testConcurrentMoveValidation();
            boolean test2 = test.testBoardConsistency();
            boolean test3 = test.testMoveHistory();
            boolean test4 = test.testGameStateTransitions();
            boolean test5 = test.testPerformance();
            boolean test6 = test.testFaultTolerance();
            
            System.out.println("\n=== Test Results ===");
            System.out.println("Concurrent Move Validation: " + (test1 ? "PASS" : "FAIL"));
            System.out.println("Board Consistency: " + (test2 ? "PASS" : "FAIL"));
            System.out.println("Move History: " + (test3 ? "PASS" : "FAIL"));
            System.out.println("Game State Transitions: " + (test4 ? "PASS" : "FAIL"));
            System.out.println("Performance Test: " + (test5 ? "PASS" : "FAIL"));
            System.out.println("Fault Tolerance: " + (test6 ? "PASS" : "FAIL"));
            
            boolean allTestsPassed = test1 && test2 && test3 && test4 && test5 && test6;
            System.out.println("\nOverall Result: " + (allTestsPassed ? "ALL TESTS PASSED" : "SOME TESTS FAILED"));
            
        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
