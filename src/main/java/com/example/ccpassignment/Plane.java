package com.example.ccpassignment;

import java.util.Random;

public class Plane extends Thread {
    private final String planeId;
    private final AirTrafficController atc;
    private final RefuelTruck refuelTruck;

    public Plane(String planeId, AirTrafficController atc, RefuelTruck refuelTruck) {
        super(planeId);
        this.planeId = planeId;
        this.atc = atc;
        this.refuelTruck = refuelTruck;
    }

    public String getPlaneId() {
        return planeId;
    }

    @Override
    public void run() {
        System.out.println(planeId + ": Requesting permission to land...");

        boolean landed = false;
        while (!landed) {
            if (atc.requestLanding(this)) {
                System.out.println(planeId + ": Landing...");
                sleepRandom();
                System.out.println(planeId + ": Landed and coasting to gate...");
                sleepRandom();

                System.out.println(planeId + ": Docked. Disembarking/Embarking passengers...");
                sleepRandom();
                System.out.println(planeId + ": Re-supplying and cleaning...");
                sleepRandom();

                System.out.println(planeId + ": Requesting refuel...");
                refuelTruck.refuel(planeId);

                System.out.println(planeId + ": Requesting takeoff...");
                sleepRandom();
                System.out.println(planeId + ": Taking off...");
                atc.releaseGate(this);
                atc.releaseRunway();

                System.out.println(planeId + ": Successfully took off.");
                landed = true;
            } else {
                System.out.println(planeId + ": Landing denied. Airport full. Waiting...");
                sleepRandom();
            }
        }
    }

    private void sleepRandom() {
        try {
            Thread.sleep(new Random().nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}