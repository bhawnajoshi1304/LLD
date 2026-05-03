package com.lld.chessengine.model;

import com.lld.chessengine.strategy.*;

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

    public boolean validateMove(Position from, Position to, Color playerColor) {
        Piece piece = getPiece(from);
        if (piece == null) return false;
        if (piece.getColor() != playerColor) return false;
        return piece.getMoveStrategy().possibleMoves(from,this).contains(to);
    }

    public boolean validateMove(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece == null) return false;
        return piece.getMoveStrategy().possibleMoves(from,this).contains(to);
    }

    public boolean movePiece(Position from, Position to, Color playerColor) {
        if (!validateMove(from, to, playerColor)) return false;

        if (!isValidMoveWithoutLeavingKingInCheck(from, to, playerColor)) return false;

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
    
    public void putPiece(Position position, Piece piece) {
        board.put(position, piece);
        if (piece != null) {
            piece.setPosition(position);
            pieces.add(piece);
        }
    }

    public Position findKing(Color color) {
        for (Piece piece : pieces) {
            if (piece.getType() == Pieces.KING && piece.getColor() == color && piece.getPosition() != null) {
                return piece.getPosition();
            }
        }
        return null;
    }

    public boolean isKingInCheck(Color kingColor) {
        Position kingPos = findKing(kingColor);
        if (kingPos == null) return false;

        Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        List<Piece> opponentPieces = getActivePieces(opponentColor);

        for (Piece opponentPiece : opponentPieces) {
            List<Position> possibleMoves = opponentPiece.getMoveStrategy().possibleMoves(opponentPiece.getPosition(), this);
            if (possibleMoves.contains(kingPos)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyValidMove(Color playerColor) {
        List<Piece> playerPieces = getActivePieces(playerColor);
        for (Piece piece : playerPieces) {
            Position from = piece.getPosition();
            List<Position> possibleMoves = piece.getMoveStrategy().possibleMoves(from, this);
            for (Position to : possibleMoves) {
                if (isValidMoveWithoutLeavingKingInCheck(from, to, playerColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidMoveWithoutLeavingKingInCheck(Position from, Position to, Color playerColor) {
        Piece movingPiece = getPiece(from);
        Piece capturedPiece = getPiece(to);

        board.put(from, null);
        board.put(to, movingPiece);
        movingPiece.setPosition(to);
        if (capturedPiece != null) {
            capturedPiece.setPosition(null);
        }

        boolean kingInCheck = isKingInCheck(playerColor);

        board.put(from, movingPiece);
        board.put(to, capturedPiece);
        movingPiece.setPosition(from);
        if (capturedPiece != null) {
            capturedPiece.setPosition(to);
        }

        return !kingInCheck;
    }

    public boolean isCheckmate(Color playerColor) {
        return isKingInCheck(playerColor) && !hasAnyValidMove(playerColor);
    }

    public boolean isStalemate(Color playerColor) {
        return !isKingInCheck(playerColor) && !hasAnyValidMove(playerColor);
    }

    public boolean onlyKingsRemain() {
        List<Piece> whitePieces = getActivePieces(Color.WHITE);
        List<Piece> blackPieces = getActivePieces(Color.BLACK);
        
        return whitePieces.size() == 1 && blackPieces.size() == 1
                && whitePieces.get(0).getType() == Pieces.KING
                && blackPieces.get(0).getType() == Pieces.KING;
    }

    public boolean isInsufficientMaterial() {
        List<Piece> whitePieces = getActivePieces(Color.WHITE);
        List<Piece> blackPieces = getActivePieces(Color.BLACK);
        
        if (whitePieces.size() == 1 && blackPieces.size() == 1) {
            return whitePieces.get(0).getType() == Pieces.KING 
                    && blackPieces.get(0).getType() == Pieces.KING;
        }
        
        if (whitePieces.size() == 1 && blackPieces.size() == 2) {
            Piece whiteKing = whitePieces.get(0);
            if (whiteKing.getType() != Pieces.KING) return false;
            
            boolean hasKing = false;
            boolean hasMinorPiece = false;
            for (Piece piece : blackPieces) {
                if (piece.getType() == Pieces.KING) hasKing = true;
                if (piece.getType() == Pieces.BISHOP || piece.getType() == Pieces.KNIGHT) hasMinorPiece = true;
            }
            return hasKing && hasMinorPiece;
        }
        
        if (blackPieces.size() == 1 && whitePieces.size() == 2) {
            Piece blackKing = blackPieces.get(0);
            if (blackKing.getType() != Pieces.KING) return false;
            
            boolean hasKing = false;
            boolean hasMinorPiece = false;
            for (Piece piece : whitePieces) {
                if (piece.getType() == Pieces.KING) hasKing = true;
                if (piece.getType() == Pieces.BISHOP || piece.getType() == Pieces.KNIGHT) hasMinorPiece = true;
            }
            return hasKing && hasMinorPiece;
        }
        
        if (whitePieces.size() == 2 && blackPieces.size() == 2) {
            boolean whiteHasKingBishop = hasKingAndBishop(whitePieces);
            boolean blackHasKingBishop = hasKingAndBishop(blackPieces);
            
            if (whiteHasKingBishop && blackHasKingBishop) {
                Piece whiteBishop = getBishop(whitePieces);
                Piece blackBishop = getBishop(blackPieces);
                if (whiteBishop != null && blackBishop != null) {
                    return bishopsOnSameColor(whiteBishop, blackBishop);
                }
            }
        }
        
        return false;
    }
    
    private boolean hasKingAndBishop(List<Piece> pieces) {
        boolean hasKing = false;
        boolean hasBishop = false;
        for (Piece piece : pieces) {
            if (piece.getType() == Pieces.KING) hasKing = true;
            if (piece.getType() == Pieces.BISHOP) hasBishop = true;
        }
        return hasKing && hasBishop;
    }
    
    private Piece getBishop(List<Piece> pieces) {
        for (Piece piece : pieces) {
            if (piece.getType() == Pieces.BISHOP) return piece;
        }
        return null;
    }
    
    private boolean bishopsOnSameColor(Piece bishop1, Piece bishop2) {
        Position pos1 = bishop1.getPosition();
        Position pos2 = bishop2.getPosition();
        if (pos1 == null || pos2 == null) return false;
        
        int sum1 = pos1.getRow() + (pos1.getColumn() - 'A' + 1);
        int sum2 = pos2.getRow() + (pos2.getColumn() - 'A' + 1);
        
        return (sum1 % 2) == (sum2 % 2);
    }
}
