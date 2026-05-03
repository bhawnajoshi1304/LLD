# TicTacToe

A Low Level Design implementation of a TicTacToe game demonstrating various design patterns.

## UML Diagram

[![Class Diagram](https://tinyurl.com/29bqkyhv)](https://tinyurl.com/29bqkyhv)<!--![Class Diagram](./UMLClass.puml)-->

## Sequence Diagram

[![Sequence Diagram](https://tinyurl.com/2a9tm98s)](https://tinyurl.com/2a9tm98s)<!--![Sequence Diagram](./Sequence.puml)-->

## Design Patterns Used

| Pattern | Package | Description |
|---------|---------|-------------|
| **State** | `model` | Game state management (Won, Draw, InProgress) |
| **Strategy** | `strategy` | Player strategies (HumanPlayerStrategy, ComputerPlayerStrategy) |
| **Observer** | `observer` | Game event listeners (FileLoggerListener) |
| **Factory** | `factory` | Player creation |
