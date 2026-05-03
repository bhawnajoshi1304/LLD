# ParkingLot

A Low Level Design implementation of a Parking Lot Management System demonstrating various design patterns.

## UML Diagram

[![Class Diagram](https://tinyurl.com/28jt3rn6)](https://tinyurl.com/28jt3rn6)<!--![Class Diagram](./UMLClass.puml)-->

## Sequence Diagram

[![Sequence Diagram](https://tinyurl.com/27penct5)](https://tinyurl.com/27penct5)<!--![Sequence Diagram](./Sequence.puml)-->

## Design Patterns Used

| Pattern | Package | Description |
|---------|---------|-------------|
| **State** | `state` | Parking spot states (Available, Occupied, OutOfService) |
| **Strategy** | `strategy` | Allocation (FlexibleAllocationStrategy, ThreadSafeParkingStrategy), Fee calculation (BasicFeeStrategy, ThreadSafePaymentStrategy), Payment methods |
| **Observer** | `observer` | Entry/exit event handling |
| **Factory** | `model.factory` | ParkingSpot, Vehicle, ParkingLotManager creation |
| **Singleton** | `model` | ParkingLot instance |
