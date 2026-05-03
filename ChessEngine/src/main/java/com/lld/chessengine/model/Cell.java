package com.lld.chessengine.model;

import com.lld.chessengine.strategy.Piece;
import com.lld.chessengine.strategy.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cell {
    private Position position;
    private Piece piece;

    public Cell(Position position, Piece piece) {
        this.position = position;
        this.piece = piece;
    }

    public boolean isEmpty(){
        return this.piece == null;
    }
    public void removePiece() {
        this.piece = null;
    }
}
