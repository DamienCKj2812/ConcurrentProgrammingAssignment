package com.example.ccpassignment;

import java.util.concurrent.Semaphore;

public class RefuelTruck {
    private final Semaphore refuelPermit = new Semaphore(1);

    public void refuel(String planeName) {
        try {
            refuelPermit.acquire();
            System.out.println("ATC: Refuelling started for " + planeName);
            Thread.sleep(1000 + new java.util.Random().nextInt(2000));
            System.out.println("ATC: Refuelling completed for " + planeName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            refuelPermit.release();
        }
    }
}
