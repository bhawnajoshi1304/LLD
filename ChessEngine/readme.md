# ChessEngine

A Low Level Design implementation of a Chess Game engine demonstrating various design patterns.

## UML Diagram

[![Class Diagram](https://tinyurl.com/289jjdtz)](https://tinyurl.com/289jjdtz)<!--![Class Diagram](./UMLClass.puml)-->

## Sequence Diagram

[![Sequence Diagram](https://tinyurl.com/26rj44f8)](https://tinyurl.com/26rj44f8)<!--![Sequence Diagram](./Sequence.puml)-->

## Design Patterns Used

| Pattern | Package | Description |
|---------|---------|-------------|
| **Strategy** | `strategy` | Different move strategies for each piece type, Player strategies (HumanPlayerStrategy, ComputerPlayerStrategy) |
| **State** | `state` | Game state management (Active, Check, Checkmate, Stalemate) |
| **Factory** | `strategy` | Piece creation via PieceFactory |
