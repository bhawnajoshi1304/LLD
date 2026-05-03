package com.lld.chessengine.model;

import com.lld.chessengine.strategy.Move;
import com.lld.chessengine.strategy.PlayerStrategy;
import com.lld.chessengine.strategy.Position;
import com.lld.chessengine.strategy.PositionRegistry;
import com.lld.chessengine.state.GameState;
import com.lld.chessengine.state.ActiveState;
import com.lld.chessengine.state.CheckState;
import com.lld.chessengine.state.CheckmateState;
import com.lld.chessengine.state.StalemateState;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class ThreadSafeEngine extends Engine {
    private final AtomicReference<GameState> gameStatus;
    private final AtomicBoolean gameActive;
    private final ReentrantReadWriteLock gameLock;
    private final ExecutorService moveValidationExecutor;
    private final ExecutorService gameStateExecutor;
    private final Map<String, Move> moveHistory;
    private final AtomicInteger moveCounter;
    private final AtomicBoolean kingInCheck;
    private final Map<Color, Position> kingPositions;
    private static final Logger logger = Logger.getLogger(ThreadSafeEngine.class.getName());
    
    private final Map<String, Boolean> checkValidationCache;
    private final AtomicInteger cacheHits;
    private final AtomicInteger cacheMisses;
    
    public ThreadSafeEngine() {
        this.gameStatus = new AtomicReference<>(new ActiveState());
        this.gameActive = new AtomicBoolean(false);
        this.gameLock = new ReentrantReadWriteLock(true);
        this.moveHistory = new ConcurrentHashMap<>();
        this.moveCounter = new AtomicInteger(0);
        this.kingInCheck = new AtomicBoolean(false);
        this.kingPositions = new ConcurrentHashMap<>();
        this.checkValidationCache = new ConcurrentHashMap<>();
        this.cacheHits = new AtomicInteger(0);
        this.cacheMisses = new AtomicInteger(0);
        
        this.moveValidationExecutor = Executors.newFixedThreadPool(
            4,
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "MoveValidation-" + threadNumber.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            }
        );
        this.gameStateExecutor = Executors.newFixedThreadPool(
            2,
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "GameState-" + threadNumber.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            }
        );
        
        logger.info("ThreadSafeEngine initialized with concurrent validation");
    }
    
    @Override
    public void startGame(String name1, String name2, PlayerStrategy strategy1, PlayerStrategy strategy2) throws RuntimeException {
        try {
            gameLock.writeLock().lock();
            
            whitePlayer = new Player(name1, Color.WHITE);
            blackPlayer = new Player(name2, Color.BLACK);
            whiteStrategy = strategy1;
            blackStrategy = strategy2;
            currentPlayer = whitePlayer;
            board = new Board();
            board.initializeBoard();
            
            initializeKingPositions();
            
            gameActive.set(true);
            gameStatus.set(new ActiveState());
            moveHistory.clear();
            moveCounter.set(0);
            kingInCheck.set(false);
            checkValidationCache.clear();
            
            board.printBoard();
            logger.info("Game started between " + name1 + " and " + name2);
            
        } finally {
            gameLock.writeLock().unlock();
        }
        
        play();
    }
    
    private void initializeKingPositions() {
                for (int row = 1; row <= 8; row++) {
            for (char col = 'A'; col <= 'H'; col++) {
                Position pos = PositionRegistry.get(col, row);
                var piece = board.getPiece(pos);
                if (piece != null && piece.getType().name().equals("KING")) {
                    kingPositions.put(piece.getColor(), pos);
                }
            }
        }
    }
    
    @Override
    public void play() {
        Scanner scanner = new Scanner(System.in);
        int consecutiveErrors = 0;
        final int MAX_CONSECUTIVE_ERRORS = 3;
        
        while (gameActive.get() && !Thread.currentThread().isInterrupted()) {
            try {
                System.out.println(currentPlayer.getName() + "'s move (e.g., E2 E4): ");
                String fromStr = scanner.next();
                String toStr = scanner.next();
                
                if (!isValidInputFormat(fromStr, toStr)) {
                    throw new IllegalArgumentException("Invalid input format");
                }
                
                Position from = PositionRegistry.get(fromStr.charAt(0), parseInt("" + fromStr.charAt(1)));
                Position to = PositionRegistry.get(toStr.charAt(0), parseInt("" + toStr.charAt(1)));
                
                if (validateAndExecuteMove(from, to)) {
                    board.printBoard();
                    togglePlayer();
                    
                    updateGameStateAsync();
                    consecutiveErrors = 0;
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            } catch (Exception e) {
                consecutiveErrors++;
                logger.log(Level.WARNING, "Error during move input (attempt " + consecutiveErrors + "/" + MAX_CONSECUTIVE_ERRORS + ")", e);
                
                if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                    logger.log(Level.SEVERE, "Too many consecutive errors, entering recovery mode");
                    enterRecoveryMode();
                    consecutiveErrors = 0;
                } else {
                    System.out.println("Invalid input format. Please use format like 'E2 E4'");
                    scanner.nextLine();
                }
            }
        }
        
        scanner.close();
        shutdown();
    }
    
    private boolean isValidInputFormat(String fromStr, String toStr) {
        if (fromStr == null || toStr == null || 
            fromStr.length() < 2 || toStr.length() < 2) {
            return false;
        }
        
        char fromCol = fromStr.charAt(0);
        char toCol = toStr.charAt(0);
        if (fromCol < 'A' || fromCol > 'H' || toCol < 'A' || toCol > 'H') {
            return false;
        }
        
        try {
            int fromRow = parseInt(fromStr.substring(1));
            int toRow = parseInt(toStr.substring(1));
            if (fromRow < 1 || fromRow > 8 || toRow < 1 || toRow > 8) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        return true;
    }
    
    private void enterRecoveryMode() {
        logger.warning("Chess engine entering recovery mode");
        try {
            gameStatus.set(new ActiveState());
            gameActive.set(true);
            moveHistory.clear();
            moveCounter.set(0);
            kingInCheck.set(false);
            checkValidationCache.clear();
            
            initializeKingPositions();
            
            logger.info("Chess engine recovery completed");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Chess engine recovery failed", e);
        }
    }
    
    private boolean validateAndExecuteMove(Position from, Position to) {
        try {
            gameLock.readLock().lock();
            
            if (!board.validateMove(from, to)) {
                return false;
            }
            
            if (wouldLeaveKingInCheck(from, to)) {
                System.out.println("Illegal move: would leave king in check");
                return false;
            }
            
            return true;
        } finally {
            gameLock.readLock().unlock();
        }
    }
    
    private boolean wouldLeaveKingInCheck(Position from, Position to) {
        Color playerColor = currentPlayer.getColor();
        
        String cacheKey = createPositionCacheKey();
        
        Boolean cachedResult = checkValidationCache.get(cacheKey);
        if (cachedResult != null) {
            cacheHits.incrementAndGet();
            return cachedResult;
        }
        
        cacheMisses.incrementAndGet();
        
        boolean inCheck = performCheckValidation(from, to, playerColor);
        
        checkValidationCache.put(cacheKey, inCheck);
        
        return inCheck;
    }
    
    private boolean performCheckValidation(Position from, Position to, Color playerColor) {
                var movingPiece = board.getPiece(from);
        var capturedPiece = board.getPiece(to);
        
        board.putPiece(from, null);
        board.putPiece(to, movingPiece);
        
        if (movingPiece != null && movingPiece.getType().name().equals("KING")) {
            kingPositions.put(playerColor, to);
        }
        
        boolean inCheck = isKingUnderAttack(playerColor);
        
        board.putPiece(from, movingPiece);
        board.putPiece(to, capturedPiece);
        
        if (movingPiece != null && movingPiece.getType().name().equals("KING")) {
            kingPositions.put(playerColor, from);
        }
        
        return inCheck;
    }
    
    private boolean isKingUnderAttack(Color kingColor) {
        Position kingPos = kingPositions.get(kingColor);
        if (kingPos == null) {
            return false;
        }
        
                Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        var opponentPieces = board.getActivePieces(opponentColor);
        
        for (var piece : opponentPieces) {
            if (piece.getPosition() != null && 
                board.validateMove(piece.getPosition(), kingPos)) {
                return true;
            }
        }
        
        return false;
    }
    
    private String createPositionCacheKey() {
        StringBuilder key = new StringBuilder();
        for (int row = 1; row <= 8; row++) {
            for (char col = 'A'; col <= 'H'; col++) {
                Position pos = PositionRegistry.get(col, row);
                var piece = board.getPiece(pos);
                if (piece != null) {
                    key.append(col).append(row)
                        .append(piece.getType().name().charAt(0))
                        .append(piece.getColor().name().charAt(0));
                }
            }
        }
        return key.toString();
    }
    
    private void updateGameStateAsync() {
        gameStateExecutor.submit(() -> {
            try {
                updateGameState();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error updating game state", e);
            }
        });
    }
    
    private void updateGameState() {
        try {
            gameLock.writeLock().lock();
            
            if (!gameActive.get()) {
                return;
            }
            
            Color currentColor = currentPlayer.getColor();
            boolean inCheck = isKingUnderAttack(currentColor);
            kingInCheck.set(inCheck);
            
            GameState newState;
            
            if (inCheck) {
                if (isCheckmate(currentColor)) {
                    newState = new CheckmateState(currentColor);
                    gameActive.set(false);
                    logger.info("Checkmate! Game over.");
                } else {
                    newState = new CheckState(currentColor);
                }
            } else if (isStalemate(currentColor)) {
                newState = new StalemateState();
                gameActive.set(false);
                logger.info("Stalemate! Game over.");
            } else {
                newState = new ActiveState();
            }
            
            GameState oldState = gameStatus.getAndSet(newState);
            if (!oldState.getStateName().equals(newState.getStateName())) {
                logger.info("Game state changed from " + oldState.getStateName() + 
                    " to " + newState.getStateName());
                newState.onEnter(this);
            }
            
        } finally {
            gameLock.writeLock().unlock();
        }
    }
    
    private boolean isCheckmate(Color kingColor) {
                var kingPieces = board.getActivePieces(kingColor).stream()
            .filter(p -> p.getType().name().equals("KING"))
            .collect(Collectors.toList());
        
        if (kingPieces.isEmpty()) {
            return false;
        }
        
        var king = kingPieces.get(0);
        Position kingPos = king.getPosition();
        
        for (int row = Math.max(1, kingPos.getRow() - 1); 
             row <= Math.min(8, kingPos.getRow() + 1); row++) {
            for (char col = (char) Math.max('A', kingPos.getColumn() - 1); 
                 col <= Math.min('H', kingPos.getColumn() + 1); col++) {
                if (row == kingPos.getRow() && col == kingPos.getColumn()) {
                    continue;
                }
                
                Position to = PositionRegistry.get(col, row);
                if (board.validateMove(kingPos, to) && 
                    !wouldLeaveKingInCheck(kingPos, to)) {
                    return false;
                }
            }
        }
        
                var allPieces = board.getActivePieces(kingColor);
        for (var piece : allPieces) {
            if (piece.getType().name().equals("KING")) {
                continue;
            }
            
            Position from = piece.getPosition();
            var possibleMoves = piece.getMoveStrategy().possibleMoves(from, board);
            
            for (Position to : possibleMoves) {
                if (!wouldLeaveKingInCheck(from, to)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private boolean isStalemate(Color kingColor) {
                if (kingInCheck.get()) {
            return false;
        }
        
        var allPieces = board.getActivePieces(kingColor);
        for (var piece : allPieces) {
            Position from = piece.getPosition();
            var possibleMoves = piece.getMoveStrategy().possibleMoves(from, board);
            
            for (Position to : possibleMoves) {
                if (!wouldLeaveKingInCheck(from, to)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private void togglePlayer() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
        logger.fine("Turn switched to " + currentPlayer.getName());
    }
    
        public GameState getGameStatus() {
        return gameStatus.get();
    }
    
    public boolean isGameActive() {
        return gameActive.get();
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public Map<String, Move> getMoveHistory() {
        return Map.copyOf(moveHistory);
    }
    
    public int getMoveCount() {
        return moveCounter.get();
    }
    
    public boolean isKingInCheck() {
        return kingInCheck.get();
    }
    
        public int getCacheHits() {
        return cacheHits.get();
    }
    
    public int getCacheMisses() {
        return cacheMisses.get();
    }
    
    public double getCacheHitRatio() {
        int hits = cacheHits.get();
        int misses = cacheMisses.get();
        int total = hits + misses;
        return total > 0 ? (double) hits / total : 0.0;
    }
    
    public void clearCache() {
        checkValidationCache.clear();
        cacheHits.set(0);
        cacheMisses.set(0);
        logger.info("Check validation cache cleared");
    }
    
    public void shutdown() {
        logger.info("Shutting down ThreadSafeEngine");
        
        gameActive.set(false);
        
        moveValidationExecutor.shutdown();
        gameStateExecutor.shutdown();
        
        try {
            if (!moveValidationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                moveValidationExecutor.shutdownNow();
            }
            if (!gameStateExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                gameStateExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            moveValidationExecutor.shutdownNow();
            gameStateExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("ThreadSafeEngine shutdown complete");
    }
}
