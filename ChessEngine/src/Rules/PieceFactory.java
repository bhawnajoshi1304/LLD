package Rules;

import Game.Color;

public class PieceFactory {
    public static Piece getPiece(Pieces piece, Color color) throws RuntimeException {
        switch (piece) {
            case ROOK -> {
                return new Piece(color, color == Color.WHITE ? "R" : "r", Pieces.ROOK, RookMoveStrategy.INSTANCE);
            }
            case QUEEN -> {
                return new Piece(color, color == Color.WHITE ? "Q" : "q", Pieces.QUEEN, QueenMoveStrategy.INSTANCE);
            }
            case BISHOP -> {
                return new Piece(color, color == Color.WHITE ? "B" : "b", Pieces.BISHOP, BishopMoveStrategy.INSTANCE);
            }
            case KNIGHT -> {
                return new Piece(color, color == Color.WHITE ? "N" : "n", Pieces.KNIGHT, KnightMoveStrategy.INSTANCE);
            }
            case KING -> {
                return new Piece(color, color == Color.WHITE ? "K" : "k", Pieces.KING, KingMoveStrategy.INSTANCE);
            }
            case PAWN -> {
                return new Piece(color, color == Color.WHITE ? "P" : "p", Pieces.PAWN, PawnMoveStrategy.INSTANCE);
            }
        }
        System.out.println(piece.toString()+color.toString());
        throw new IllegalArgumentException("Unknown piece type: ");
    }
}
