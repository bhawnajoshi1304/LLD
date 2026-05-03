package com.lld.chessengine.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    private Position from;
    private Position to;
    private Piece movedPiece;
    private Piece capturedPiece;
    
    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
        this.movedPiece = null;
        this.capturedPiece = null;
    }
}
