package com.example.ccpassignment;

import java.util.Random;

public class Plane extends Thread {
    private final String planeId;
    private final AirTrafficController atc;
    private final RefuelTruck refuelTruck;
    private final boolean emergency;
    private final int passengerCount;
    private int assignedGate = -1;
    private boolean landed = false;

    public Plane(String planeId, AirTrafficController atc, RefuelTruck refuelTruck, boolean emergency) {
        this.planeId = planeId;
        this.atc = atc;
        this.refuelTruck = refuelTruck;
        this.emergency = emergency;
        this.passengerCount = new Random().nextInt(50); // Random passenger count between 0 and 50 
    }

    @Override
    public void run() {
        while (!landed) {
            if (atc.requestLanding(this)) {
                System.out.println(planeId + ": Landing.");
                sleepRandom();

                System.out.println(planeId + ": Landed.");
                System.out.println(planeId + ": Coasting to " + atc.getAssignedGateString(planeId) + ".");
                sleepRandom();

                System.out.println(planeId + ": Docked at " + atc.getAssignedGateString(planeId) + ".");
                System.out.println(planeId + "'s Passengers: " + passengerCount + " passengers are disembarking out of "
                        + planeId + ".");
                sleepRandom();

                System.out.println(planeId + ": Re-supplying and cleaning.");
                sleepRandom();

                System.out.println(planeId + ": Requesting refuel...");
                refuelTruck.refuel(planeId);

                System.out.println(planeId + ": Requesting Taking off.");
                System.out.println("ATC: Taking-off is granted for " + planeId + ". Runway is free.");
                sleepRandom();

                System.out.println(planeId + ": Taking-off.");
                atc.releaseGate(this);
                atc.releaseRunway();

                System.out.println(planeId + ": Successfully took off.");
                landed = true;
            } else {
                System.out.println(planeId + ": Requesting Landing.");
                System.out.println("ATC: Landing Permission Denied for " + planeId + ", Airport Full.");
                try {
                    Thread.sleep(2000); // Wait 2 seconds before trying again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sleepRandom() {
        try {
            Thread.sleep(new Random().nextInt(1500) + 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getPlaneId() {
        return planeId;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public int getAssignedGate() {
        return assignedGate;
    }

    public void setAssignedGate(int gate) {
        this.assignedGate = gate;
    }
}
