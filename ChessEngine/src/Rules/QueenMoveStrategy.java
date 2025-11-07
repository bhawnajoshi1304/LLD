package Rules;

import Game.Board;

import java.util.ArrayList;
import java.util.List;

public class QueenMoveStrategy implements MoveStrategy {

    public static final QueenMoveStrategy INSTANCE = new QueenMoveStrategy();

    private static final RookMoveStrategy rook = RookMoveStrategy.INSTANCE;
    private static final BishopMoveStrategy bishop = BishopMoveStrategy.INSTANCE;

    private QueenMoveStrategy() {}

    @Override
    public List<Position> possibleMoves(Position fromPosition, Board board) {
        List<Position> moves = new ArrayList<>();
        moves.addAll(rook.possibleMoves(fromPosition, board));
        moves.addAll(bishop.possibleMoves(fromPosition, board));
        return moves;
    }
}