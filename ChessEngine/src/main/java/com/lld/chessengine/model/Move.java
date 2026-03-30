package com.lld.chessengine.model;

import com.lld.chessengine.strategy.Piece;
import com.lld.chessengine.strategy.Position;

public class Move {
    private Piece movedPiece;
    private Position start;
    private Position end;
    private Piece capturedPiece;
}
