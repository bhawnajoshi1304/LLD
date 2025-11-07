package Game;

import Rules.*;

import java.util.*;

public class Board {
    private final Map<Position, Piece> board;
    private final Set<Piece> pieces;

    private final Map<Color, Boolean> kingSideCastling = new HashMap<>();
    private final Map<Color, Boolean> queenSideCastling = new HashMap<>();
    private Position enPassantTarget = null;

    public Board() {
        this.board = new HashMap<>();
        this.pieces = new HashSet<>();
    }

    public void initializeBoard() throws RuntimeException {
        for (Position pos : PositionRegistry.allPositions()) {
            board.put(pos, null);
        }

        setupBackRow(Color.WHITE, 1);
        setupPawnRow(Color.WHITE, 2);
        setupBackRow(Color.BLACK, 8);
        setupPawnRow(Color.BLACK, 7);

        kingSideCastling.put(Color.WHITE, true);
        queenSideCastling.put(Color.WHITE, true);
        kingSideCastling.put(Color.BLACK, true);
        queenSideCastling.put(Color.BLACK, true);
    }

    private void setupBackRow(Color color, int row) throws RuntimeException {
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        Piece[] backRow = new Piece[]{
                PieceFactory.getPiece(Pieces.ROOK, color), PieceFactory.getPiece(Pieces.KNIGHT, color),
                PieceFactory.getPiece(Pieces.BISHOP, color), PieceFactory.getPiece(Pieces.QUEEN, color),
                PieceFactory.getPiece(Pieces.KING, color), PieceFactory.getPiece(Pieces.BISHOP, color),
                PieceFactory.getPiece(Pieces.KNIGHT, color), PieceFactory.getPiece(Pieces.ROOK, color)};

        for (int i = 0; i < 8; i++) {
            Position pos = PositionRegistry.get(cols[i], row);
            backRow[i].setPosition(pos);
            board.put(pos, backRow[i]);
            pieces.add(backRow[i]);
        }
    }

    private void setupPawnRow(Color color, int row) throws RuntimeException {
        for (char col = 'A'; col <= 'H'; col++) {
            Position pos = PositionRegistry.get(col, row);
            Piece pawn = PieceFactory.getPiece(Pieces.PAWN, color);
            pawn.setPosition(pos);
            board.put(pos, pawn);
            pieces.add(pawn);
        }
    }

    public void printBoard() {
        System.out.println("   A  B  C  D  E  F  G  H");
        System.out.println("  ------------------------");

        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " |");
            for (char col = 'A'; col <= 'H'; col++) {
                Position pos = PositionRegistry.get(col, row);
                Piece piece = board.get(pos);

                if (piece == null) {
                    System.out.print(" . ");
                } else {
                    System.out.print(" " + piece.getSymbol() + " ");
                }
            }
            System.out.println("| " + row);
        }

        System.out.println("  ------------------------");
        System.out.println("   A  B  C  D  E  F  G  H");
    }

    public Piece getPiece(Position pos) {
        return board.get(pos);
    }

    public boolean isEmpty(Position pos) {
        return board.get(pos) == null;
    }

    public boolean canCastleKingSide(Color color) {
        return kingSideCastling.getOrDefault(color, false);
    }

    public boolean canCastleQueenSide(Color color) {
        return queenSideCastling.getOrDefault(color, false);
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position pos) {
        this.enPassantTarget = pos;
    }

    public List<Piece> getActivePieces(Color color) {
        List<Piece> result = new ArrayList<>();
        for (Piece piece : pieces) {
            if (piece.getColor() == color && piece.getPosition() != null) {
                result.add(piece);
            }
        }
        return result;
    }

    public void updateCastlingRights(Piece piece, Position from) {
        if (piece.getType() == Pieces.KING) {
            kingSideCastling.put(piece.getColor(), false);
            queenSideCastling.put(piece.getColor(), false);
        } else if (piece.getType() == Pieces.ROOK) {
            if (from.getColumn() == 'A') {
                queenSideCastling.put(piece.getColor(), false);
            } else if (from.getColumn() == 'H') {
                kingSideCastling.put(piece.getColor(), false);
            }
        }
    }

    public boolean validateMove(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece == null) return false;
        return piece.getMoveStrategy().possibleMoves(from,this).contains(to);
    }

    public boolean movePiece(Position from, Position to) {
        if (!validateMove(from, to)) return false;

        Piece targetPiece = getPiece(to);
        if (targetPiece != null) targetPiece.setPosition(null);

        Piece piece = getPiece(from);
        if (piece == null) return false;

        updateCastlingRights(piece, from);

        if (piece.getType() == Pieces.PAWN && Math.abs(from.getRow() - to.getRow()) == 2) {
            setEnPassantTarget(PositionRegistry.get(from.getColumn(), (from.getRow() + to.getRow()) / 2));
        } else {
            setEnPassantTarget(null);
        }

        board.put(from, null);
        board.put(to, piece);
        piece.setPosition(to);
        return true;
    }
}
