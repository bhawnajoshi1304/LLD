package Rules;

import Game.Board;

import java.util.ArrayList;
import java.util.List;

public class KingMoveStrategy implements MoveStrategy {

    public static final KingMoveStrategy INSTANCE = new KingMoveStrategy();

    private static final int[][] KING_MOVES = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
    };

    private KingMoveStrategy() {}

    @Override
    public List<Position> possibleMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();

        for (int[] move : KING_MOVES) {
            Position next = from.offset(move[0], move[1]);
            if (next != null) {
                Piece target = board.getPiece(next);
                if (target == null || !target.getColor().equals(board.getPiece(from).getColor())) {
                    moves.add(next);
                }
            }
        }

        // Castling logic
        Piece king = board.getPiece(from);
        if (!king.getHasMoved()) {
            if (board.canCastleKingSide(king.getColor())) {
                moves.add(PositionRegistry.get('G', from.getRow()));
            }
            if (board.canCastleQueenSide(king.getColor())) {
                moves.add(PositionRegistry.get('C', from.getRow()));
            }
        }

        return moves;
    }
}
