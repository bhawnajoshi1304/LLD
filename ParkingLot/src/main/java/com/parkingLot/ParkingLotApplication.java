package main.java.com.parkingLot;

import main.java.com.parkingLot.enums.ParkingSpotType;
import main.java.com.parkingLot.enums.VehicleType;
import main.java.com.parkingLot.model.ParkingLot;
import main.java.com.parkingLot.model.User;
import main.java.com.parkingLot.strategy.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParkingLotApplication {

    private static void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
    public static void main(String[] args) {
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("issue creating user");
        }
    }
}