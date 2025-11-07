package Rules;

import Game.Board;
import Game.Color;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveStrategy implements MoveStrategy{
    public static final PawnMoveStrategy INSTANCE = new PawnMoveStrategy();
    public static final int[][] DIRECTIONS = {};
    @Override
    public List<Position> possibleMoves(Position fromPosition, Board board) {
        List<Position> moves = new ArrayList<>();
        Piece pawn = board.getPiece(fromPosition);
        int direction = pawn.getColor().equals(Color.WHITE) ? 1 : -1;

        // Forward move
        Position oneStep = fromPosition.offset(0, direction);
        if (oneStep != null && board.isEmpty(oneStep)) {
            moves.add(oneStep);

            // Two-step on first move
            if (!pawn.getHasMoved()) {
                Position twoStep = fromPosition.offset(0, direction * 2);
                if (board.isEmpty(twoStep)) {
                    moves.add(twoStep);
                }
            }
        }

        // Diagonal captures
        for (int dx : new int[]{-1, 1}) {
            Position diag = fromPosition.offset(dx, direction);
            if (diag != null) {
                Piece target = board.getPiece(diag);
                if (target != null && !target.getColor().equals(pawn.getColor())) {
                    moves.add(diag);
                }
            }
        }

        // En Passant
        Position enPassantTarget = board.getEnPassantTarget();
        if (enPassantTarget != null && Math.abs(enPassantTarget.getColumn() - fromPosition.getColumn()) == 1 &&
                enPassantTarget.getRow() == fromPosition.getRow() + direction) {
            moves.add(enPassantTarget);
        }

        return moves;
    }
}
