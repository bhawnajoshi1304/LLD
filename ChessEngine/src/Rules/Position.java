package Rules;

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

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return "" + column + row;
    }
}
