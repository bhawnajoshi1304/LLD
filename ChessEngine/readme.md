- ChessGame
    - Player whitePlayer
    - Player blackPlayer
    - Board board
    - Player currentTurn
    - GameStatus status
    - createBoard(); 
    - initializeColor(); 
    - placePieces(); 
    - promptToPlay();

- Player
    - Color color
    - String name
    - boolean isHuman

- Board
  - Map<Position,Cell> board; // Position -> Cell 
  - Set<Piece> pieces;         // All current active pieces on the board 
  - public Cell getCell(Position pos)
  - public List<Piece> getActivePieces(Color color) { 
    
- Cell
    - Position position;
    - Piece piece
    - isEmpty()
    - getPiece()
    - setPiece()
    - removePiece() // null

- Piece (abstract)
    - Color color
    - Position currentPosition
    - String symbol
    - MovementStrategy movementStrategy
    - abstract List<Position> getValidMoves(Board board); 
    - Subclasses: Pawn, Rook, Knight, Queen, etc. 

- Subclasses: King, Queen, Rook, Knight, Bishop, Pawn

- MovementStrategy (interface)
    - getValidMoves()

- Move
    - Piece movedPiece
    - Cell from
    - Cell to
    - Piece capturedPiece

- GameStatus (enum)
    - ONGOING, CHECK, CHECKMATE, STALEMATE, DRAW

- Position { 
  - char row; // 'A' to 'H' 
  - int col;  // 1 to 8
- Direction 
  - UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1),
  DIAGONAL_LEFT_UP(-1, -1), DIAGONAL_RIGHT_UP(-1, 1),

# ChessEngine

A Low Level Design implementation of a Chess Game engine demonstrating various design patterns.

## UML Diagram

[![Class Diagram](https://tinyurl.com/placeholder-chessengine)](https://tinyurl.com/placeholder-chessengine)<!--![Class Diagram](./UMLClass.puml)-->

## Sequence Diagram

[![Sequence Diagram](https://tinyurl.com/placeholder-chessengine-seq)](https://tinyurl.com/placeholder-chessengine-seq)<!--![Sequence Diagram](./Sequence.puml)-->

## Design Patterns Used Diagram

[![Sequence Diagram](https://tinyurl.com/placeholder-chessengine-seq)](https://tinyurl.com/placeholder-chessengine-seq)<!--![Sequence Diagram](./Sequence.puml)-->

## Design Patterns Used

- MoveStartegy initially needed just the startPosition, but king and pawn have special moves like castle and enpassant so we need board instance too
