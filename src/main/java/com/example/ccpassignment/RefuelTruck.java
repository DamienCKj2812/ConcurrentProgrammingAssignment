package com.example.ccpassignment;

import java.util.concurrent.Semaphore;

public class RefuelTruck {
    private final Semaphore refuelPermit = new Semaphore(1);

    public void refuel(String planeName) {
        try {
            refuelPermit.acquire();
            System.out.println(planeName + ": Refuelling started...");
            Thread.sleep(1000 + new java.util.Random().nextInt(2000));
            System.out.println(planeName + ": Refuelling completed.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            refuelPermit.release();
        }
    }
}