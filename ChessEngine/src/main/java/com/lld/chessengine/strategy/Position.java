package com.lld.chessengine.strategy;

import lombok.Getter;

@Getter
public class Position {
    private final char column;
    private final int row;

    public Position(char column, int row) {
        if (column < 'A' || column > 'H' || row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid position: " + column + row);
        }
        this.column = column;
        this.row = row;
    }

    public Position offset(int dCol, int dRow) {
        char newCol = (char)(column + dCol);
        int newRow = row + dRow;
        if (newCol < 'A' || newCol > 'H' || newRow < 1 || newRow > 8) {
            return null;
        }
        return new Position(newCol, newRow);
    }

    @Override
    public String toString() {
        return "" + column + row;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return column == position.column && row == position.row;
    }

    @Override
    public int hashCode() {
        return 31 * column + row;
    }
}
