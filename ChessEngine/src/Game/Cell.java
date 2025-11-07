package Game;

import Rules.Piece;
import Rules.Position;

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
    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return this.piece;
    }
}
