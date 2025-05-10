package com.example.ccpassignment;

import java.util.Random;

public class Plane extends Thread {
    private final String planeId;
    private final AirTrafficController atc;
    private final RefuelTruck refuelTruck;
    private final int passengerCount;
    private final boolean isEmergency;
    private int assignedGate = -1;

    public Plane(String planeId, AirTrafficController atc, RefuelTruck refuelTruck, boolean isEmergency) {
        super(planeId);
        this.planeId = planeId;
        this.atc = atc;
        this.refuelTruck = refuelTruck;
        this.passengerCount = 30 + new Random().nextInt(21);
        this.isEmergency = isEmergency;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public int getAssignedGate() {
        return assignedGate;
    }

    public void setAssignedGate(int gate) {
        this.assignedGate = gate;
    }

    public String getPlaneId() {
        return planeId;
    }

    public int getPassengerCount() {
        return passengerCount;
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

                System.out.println(planeId + "'s Passengers: " + passengerCount + " disembarking and embarking...");
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