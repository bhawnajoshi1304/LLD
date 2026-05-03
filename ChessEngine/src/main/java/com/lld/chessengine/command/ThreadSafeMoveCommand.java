package com.lld.chessengine.command;

import com.lld.chessengine.strategy.Piece;
import com.lld.chessengine.strategy.Pieces;
import com.lld.chessengine.strategy.Position;
import com.lld.chessengine.strategy.PositionRegistry;
import com.lld.chessengine.model.Board;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.time.LocalDateTime;

public class ThreadSafeMoveCommand {
    private final String moveId;
    private final Position from;
    private final Position to;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final AtomicBoolean executed;
    private final AtomicReference<LocalDateTime> executionTime;
    private final AtomicReference<String> moveNotation;
    private final ReentrantReadWriteLock commandLock;
    private final boolean wasCastle;
    private final boolean wasEnPassant;
    private final Position enPassantTarget;
    private final boolean promotion;
    private final Pieces promotionPiece;
    
        public ThreadSafeMoveCommand(String moveId, Position from, Position to, 
                              Piece movedPiece, Piece capturedPiece) {
        this(moveId, from, to, movedPiece, capturedPiece, false, false, null, false, null);
    }
    
        public ThreadSafeMoveCommand(String moveId, Position from, Position to, Piece movedPiece, 
                              Piece capturedPiece, boolean wasCastle, boolean wasEnPassant,
                              Position enPassantTarget, boolean promotion, Pieces promotionPiece) {
        this.moveId = moveId;
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.wasCastle = wasCastle;
        this.wasEnPassant = wasEnPassant;
        this.enPassantTarget = enPassantTarget;
        this.promotion = promotion;
        this.promotionPiece = promotionPiece;
        this.executed = new AtomicBoolean(false);
        this.executionTime = new AtomicReference<>(null);
        this.commandLock = new ReentrantReadWriteLock(true);
        this.moveNotation = new AtomicReference<>(generateNotation());
    }
    
        public boolean execute(Board board) {
        try {
            commandLock.writeLock().lock();
            
            // Prevent double execution
            if (executed.get()) {
                return false; // Already executed
            }
            
            // Validate move before execution
            if (!board.validateMove(from, to)) {
                return false; // Invalid move
            }

            boolean success = board.movePiece(from, to);
            if (success) {
                executed.set(true);
                executionTime.set(LocalDateTime.now());
                handleSpecialMoves(board, true);
                
                return true;
            }
            return false;
        } finally {
            commandLock.writeLock().unlock();
        }
    }
    
        public boolean undo(Board board) {
        try {
            commandLock.writeLock().lock();
            
            if (!executed.get()) {
                return false;
            }

            handleSpecialMoves(board, false);
            board.putPiece(to, null); // Clear destination
            board.putPiece(from, movedPiece); // Restore source
            if (capturedPiece != null) {
                board.putPiece(to, capturedPiece);
            }
            movedPiece.setPosition(from);
            if (capturedPiece != null) {
                capturedPiece.setPosition(to);
            }
            executed.set(false);
            executionTime.set(null);
            return true;
        } finally {
            commandLock.writeLock().unlock();
        }
    }
    
        private void handleSpecialMoves(Board board, boolean isExecute) {
        if (wasCastle) {
            handleCastling(board, isExecute);
        }
        
        if (wasEnPassant && enPassantTarget != null) {
            handleEnPassant(board, isExecute);
        }
        
        if (promotion && promotionPiece != null) {
            handlePromotion(board, isExecute);
        }
    }
    
    private void handleCastling(Board board, boolean isExecute) {
        if (isExecute) {
            char rookFromCol = (to.getColumn() > from.getColumn()) ? 'H' : 'A';
            char rookToCol = (to.getColumn() > from.getColumn()) ? 'F' : 'D';
            Position rookFrom = PositionRegistry.get(rookFromCol, from.getRow());
            Position rookTo = PositionRegistry.get(rookToCol, from.getRow());
            
            Piece rook = board.getPiece(rookFrom);
            if (rook != null) {
                board.movePiece(rookFrom, rookTo);
            }
        } else {
            char rookFromCol = (to.getColumn() > from.getColumn()) ? 'H' : 'A';
            char rookToCol = (to.getColumn() > from.getColumn()) ? 'F' : 'D';
            Position rookFrom = PositionRegistry.get(rookFromCol, from.getRow());
            Position rookTo = PositionRegistry.get(rookToCol, from.getRow());
            
            Piece rook = board.getPiece(rookTo);
            if (rook != null) {
                board.movePiece(rookTo, rookFrom);
            }
        }
    }
    
    private void handleEnPassant(Board board, boolean isExecute) {
        if (isExecute) {
            board.putPiece(enPassantTarget, null);
        } else {
            if (capturedPiece != null) {
                board.putPiece(enPassantTarget, capturedPiece);
            }
        }
    }
    
    private void handlePromotion(Board board, boolean isExecute) {
        if (isExecute) {
            // Promote pawn
            // Implementation would depend on PieceFactory
        } else {
            // Restore pawn
            // Implementation would restore original pawn
        }
    }
    
        private String generateNotation() {
        StringBuilder notation = new StringBuilder();
        
        if (wasCastle) {
            if (to.getColumn() > from.getColumn()) {
                return "O-O";
            } else {
                return "O-O-O";
            }
        }

        if (movedPiece != null) {
            String pieceSymbol = movedPiece.getSymbol();
            if (!pieceSymbol.equals("P")) {
                notation.append(pieceSymbol);
            }
        }

        notation.append(from.getColumn()).append(from.getRow());

        if (capturedPiece != null) {
            notation.append("x");
        }

        notation.append(to.getColumn()).append(to.getRow());

        if (promotion && promotionPiece != null) {
            notation.append("=").append(promotionPiece.name().charAt(0));
        }

        if (wasEnPassant) {
            notation.append(" e.p.");
        }
        
        return notation.toString();
    }

    public String getMoveId() {
        return moveId;
    }
    
    public Position getFrom() {
        return from;
    }
    
    public Position getTo() {
        return to;
    }
    
    public Piece getMovedPiece() {
        return movedPiece;
    }
    
    public Piece getCapturedPiece() {
        return capturedPiece;
    }
    
    public boolean isExecuted() {
        return executed.get();
    }
    
    public LocalDateTime getExecutionTime() {
        return executionTime.get();
    }
    
    public String getMoveNotation() {
        return moveNotation.get();
    }
    
    public boolean wasCastle() {
        return wasCastle;
    }
    
    public boolean wasEnPassant() {
        return wasEnPassant;
    }
    
    public boolean wasPromotion() {
        return promotion;
    }
    
    public Pieces getPromotionPiece() {
        return promotionPiece;
    }
    
        public void updateNotation(String newNotation) {
        try {
            commandLock.writeLock().lock();
            String oldNotation = moveNotation.getAndSet(newNotation);
        } finally {
            commandLock.writeLock().unlock();
        }
    }
    
        public long getExecutionDurationMillis() {
        LocalDateTime execTime = executionTime.get();
        return execTime != null ? 
            java.time.Duration.between(execTime, LocalDateTime.now()).toMillis() : -1;
    }
    
    @Override
    public String toString() {
        return String.format("MoveCommand{id=%s, notation=%s, executed=%s, time=%s}", 
            moveId, moveNotation.get(), executed.get(), executionTime.get());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ThreadSafeMoveCommand that = (ThreadSafeMoveCommand) obj;
        return moveId.equals(that.moveId);
    }
    
    @Override
    public int hashCode() {
        return moveId.hashCode();
    }
}
