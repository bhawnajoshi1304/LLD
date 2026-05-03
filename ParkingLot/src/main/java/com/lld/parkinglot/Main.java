package com.lld.parkinglot;

import com.lld.parkinglot.enums.ParkingSpotType;
import com.lld.parkinglot.enums.VehicleType;
import com.lld.parkinglot.model.ParkingLot;
import com.lld.parkinglot.model.User;
import com.lld.parkinglot.strategy.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static void setupLogging() {
        try {
            String logPath = System.getProperty("user.dir") + "/../../logs/ParkingLot/parkinglot.log";
            FileHandler fileHandler = new FileHandler(logPath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Failed to setup logging: " + e.getMessage());
        }
    }

    private static boolean getChoice(String message) {
        System.out.println(message);
        while (true) {
            String choice = scanner.nextLine().trim().toUpperCase();
            if (choice.equals("Y") || choice.equals("D")) {
                return true;
            } else if (choice.equals("N") || choice.equals("M")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'Y/D' for demo or 'N/M' for manual.");
            }
        }
    }

    private static void runDemoMode(ParkingLot pk) {
        System.out.println("=== Running Demo Mode ===");
        PaymentStrategy card = new CardPayment(), cash = new CashPayment(), upi = new UPIPayment();
        try {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            Runnable task = () -> {
                User user = new User(VehicleType.MOTORBIKE,"UK01AB1234",card);
                pk.park(user);
                sleep(300);
                pk.exit(user);
            };
            Runnable task0 = () -> {
                User user = new User(VehicleType.TRUCK, "KA01AB1234",upi);
                pk.park(user);
                sleep(600);
                pk.exit(user);
            };
            Runnable task1 = () -> {
                User user = new User(VehicleType.CAR,"MH01AB1111",cash);
                pk.park(user);
                sleep(1000);
                pk.exit(user);
            };

            Runnable task2 = () -> {
                User user = new User(VehicleType.CAR,"MH01AB1211",upi);
                pk.park(user);
                sleep(1500);
                pk.exit(user);
            };

            Runnable task3 = () -> {
                User user = new User(VehicleType.CAR,"MH01AB1121",cash);
                pk.park(user);
                sleep(500);
                pk.exit(user);
            };

            executor.execute(task);
            executor.execute(task0);
            executor.execute(task1);
            executor.execute(task2);
            executor.execute(task3);
            executor.shutdown();
            executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
            System.out.println("=== Demo Complete ===");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("issue creating user");
        }
    }

    private static void runManualMode(ParkingLot pk) {
        System.out.println("=== Manual Mode ===");
        PaymentStrategy card = new CardPayment(), cash = new CashPayment(), upi = new UPIPayment();
        
        while (true) {
            System.out.println("\n1. Park Vehicle");
            System.out.println("2. Exit Vehicle");
            System.out.println("3. View Parking Lot Status");
            System.out.println("4. Exit Manual Mode");
            System.out.print("Choose option: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    parkVehicle(pk);
                    break;
                case "2":
                    exitVehicle(pk);
                    break;
                case "3":
                    viewStatus(pk);
                    break;
                case "4":
                    System.out.println("Exiting Manual Mode...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void parkVehicle(ParkingLot pk) {
        System.out.println("\n--- Park Vehicle ---");
        System.out.println("Vehicle Types: 1. MOTORBIKE, 2. CAR, 3. TRUCK");
        System.out.print("Enter vehicle type (1-3): ");
        String typeChoice = scanner.nextLine().trim();
        
        VehicleType vehicleType;
        switch (typeChoice) {
            case "1": vehicleType = VehicleType.MOTORBIKE; break;
            case "2": vehicleType = VehicleType.CAR; break;
            case "3": vehicleType = VehicleType.TRUCK; break;
            default:
                System.out.println("Invalid vehicle type.");
                return;
        }
        
        System.out.print("Enter vehicle number: ");
        String numberPlate = scanner.nextLine().trim();
        
        System.out.println("Payment Methods: 1. CARD, 2. CASH, 3. UPI");
        System.out.print("Enter payment method (1-3): ");
        String paymentChoice = scanner.nextLine().trim();
        
        PaymentStrategy payment;
        switch (paymentChoice) {
            case "1": payment = new CardPayment(); break;
            case "2": payment = new CashPayment(); break;
            case "3": payment = new UPIPayment(); break;
            default:
                System.out.println("Invalid payment method.");
                return;
        }
        
        try {
            User user = new User(vehicleType, numberPlate, payment);
            pk.park(user);
            System.out.println("Vehicle parked successfully!");
        } catch (Exception e) {
            System.out.println("Error parking vehicle: " + e.getMessage());
        }
    }

    private static void exitVehicle(ParkingLot pk) {
        System.out.println("\n--- Exit Vehicle ---");
        System.out.print("Enter vehicle number: ");
        String numberPlate = scanner.nextLine().trim();
        
        try {
            User user = new User(null, numberPlate, null);
            pk.exit(user);
            System.out.println("Vehicle exited successfully!");
        } catch (Exception e) {
            System.out.println("Error exiting vehicle: " + e.getMessage());
        }
    }

    private static void viewStatus(ParkingLot pk) {
        System.out.println("\n--- Parking Lot Status ---");
        System.out.println("Total Capacity: " + pk.getTotalCapacity());
        System.out.println("Occupied Spots: " + pk.getOccupiedSpots());
        System.out.println("Available Spots: " + pk.getAvailableSpots());
        System.out.println("Is Full: " + pk.isFull());
        System.out.println("Is Healthy: " + pk.isHealthy());
        System.out.println("Floor Occupancy: " + pk.getOccupancyByFloor());
    }

    public static void main(String[] args) {
        setupLogging();
        
        System.out.println("=== Parking Lot System ===");
        boolean demoMode;
        
        if (args.length > 0 && (args[0].equalsIgnoreCase("--demo") || args[0].equalsIgnoreCase("-d"))) {
            demoMode = true;
        } else if (args.length > 0 && (args[0].equalsIgnoreCase("--manual") || args[0].equalsIgnoreCase("-m"))) {
            demoMode = false;
        } else {
            demoMode = getChoice("Run Demo Mode? (Y/D for Yes, N/M for Manual): ");
        }
        
        FeeStrategy feeStrategy = new BasicFeeStrategy();
        AllocationStrategy allocationStrategy = new FlexibleAllocationStrategy();
        int floors = 3;
        List<Map<ParkingSpotType, Integer>> floorMap = new ArrayList<>();
        for(int i=0;i<floors;i+=1){
            Map<ParkingSpotType,Integer> mp = new HashMap<>();
            mp.put(ParkingSpotType.SMALL,5);
            mp.put(ParkingSpotType.BIG,10);
            mp.put(ParkingSpotType.LARGE,3);
            floorMap.add(mp);
        }
        ParkingLot pk = ParkingLot.createInstance(feeStrategy,allocationStrategy,floors,floorMap);
        
        if (demoMode) {
            runDemoMode(pk);
        } else {
            runManualMode(pk);
        }
    }
}