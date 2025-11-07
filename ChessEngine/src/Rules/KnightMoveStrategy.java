package Rules;

import Game.Board;

import java.util.List;

public class KnightMoveStrategy implements MoveStrategy{
    public static final KnightMoveStrategy INSTANCE = new KnightMoveStrategy();
    public static final int[][] DIRECTIONS = {
            {2,1}, {2,-1}, {1,2}, {-1,2}, {1,-2}, {-1,-2}, {-2,1}, {-2,-1}, {1,-2}, {-1,-2}
    };
    @Override
    public List<Position> possibleMoves(Position fromPosition, Board board) {
        return MoveUtils.positionMoves(fromPosition, DIRECTIONS);
    }
}
