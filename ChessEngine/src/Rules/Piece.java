package Rules;

import Game.Color;


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
    public Pieces getType(){
        return this.type;
    }
    public Position getPosition() {
        return this.position;
    }
    public  Color getColor(){
        return this.color;
    }
    public void setPosition(Position pos) {
        this.position = pos;
    }
    public String getSymbol(){
        return this.symbol;
    }
    public void setMoveStrategy(MoveStrategy moveStrategy) {
        this.moveStrategy = moveStrategy;
    }
    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public MoveStrategy getMoveStrategy() {
        return moveStrategy;
    }
}
