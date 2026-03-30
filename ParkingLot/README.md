# ParkingLot

A Low Level Design implementation of a Parking Lot Management System demonstrating various design patterns.

## UML Diagram

[![Class Diagram](https://tinyurl.com/placeholder-parkinglot)](https://tinyurl.com/placeholder-parkinglot)<!--![Class Diagram](./UMLClass.puml)-->

## Sequence Diagram

[![Sequence Diagram](https://tinyurl.com/placeholder-parkinglot-seq)](https://tinyurl.com/placeholder-parkinglot-seq)<!--![Sequence Diagram](./SequenceDiagram.puml)-->

## Design Patterns Used

| Pattern | Package | Description |
|---------|---------|-------------|
| **State** | `state` | Parking spot states (Available, Occupied, OutOfService) |
| **Strategy** | `strategy` | Allocation, Fee calculation, Payment methods |
| **Observer** | `observer` | Entry/exit event handling |
| **Factory** | `model.factory` | ParkingSpot, Vehicle, ParkingLotManager creation |
| **Singleton** | `model` | ParkingLot instance |
