package Rules;

import Game.Board;

import java.util.List;

public class RookMoveStrategy implements MoveStrategy {
    public static final RookMoveStrategy INSTANCE = new RookMoveStrategy();
    private RookMoveStrategy() {}

    private static final int[][] DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };
    @Override
    public List<Position> possibleMoves(Position fromPosition, Board board) {
        return MoveUtils.rayMoves(fromPosition, DIRECTIONS);
    }
}