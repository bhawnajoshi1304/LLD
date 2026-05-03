package com.lld.chessengine.strategy;

import com.lld.chessengine.model.Color;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Piece {
    private final Color color;
    private final Pieces type;
    private Position position;
    private final String symbol;
    private MoveStrategy moveStrategy;
    private boolean hasMoved = false;

    Piece(Color color, String symbol, Pieces type, MoveStrategy moveStrategy){
        this.color = color;
        this.symbol = symbol;
        this.type = type;
        this.moveStrategy = moveStrategy;
    }
    
    public boolean getHasMoved() {
        return hasMoved;
    }
}
