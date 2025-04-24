package com.example.ccpassignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class AirTrafficController {
    private final int MAX_PLANES = 3;
    private final Semaphore runway = new Semaphore(1);
    private final Semaphore groundAccess = new Semaphore(MAX_PLANES);

    private final List<Long> waitingTimes = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> passengerCounts = Collections.synchronizedList(new ArrayList<>());

    public boolean requestLanding(Plane plane) {
        long requestTime = System.currentTimeMillis();
        System.out.println("ATC: " + plane.getPlaneId() + " requesting landing...");
        if (groundAccess.tryAcquire()) {
            try {
                runway.acquire();
                long waitTime = System.currentTimeMillis() - requestTime;
                waitingTimes.add(waitTime);
                passengerCounts.add(plane.getPassengerCount());
                System.out.println("ATC: Landing permission granted for " + plane.getPlaneId());
                System.out.println("ATC: Gate assigned for " + plane.getPlaneId());
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                groundAccess.release();
            }
        } else {
            System.out.println("ATC: Landing permission denied for " + plane.getPlaneId() + ", Airport Full.");
        }
        return false;
    }

    public void releaseRunway() {
        System.out.println("ATC: Runway is now free.");
        runway.release();
    }

    public void releaseGate(Plane plane) {
        System.out.println("ATC: Gate released for " + plane.getPlaneId());
        groundAccess.release();
    }

    public void printStatistics() {
        System.out.println("\n=== AIRPORT STATISTICS ===");
        System.out.println("All gates are empty: " + (groundAccess.availablePermits() == MAX_PLANES));

        if (!waitingTimes.isEmpty()) {
            long max = waitingTimes.get(0);
            long min = waitingTimes.get(0);
            long sum = 0;
            for (long t : waitingTimes) {
                if (t > max) max = t;
                if (t < min) min = t;
                sum += t;
            }
            double avg = (double) sum / waitingTimes.size();

            int totalPassengers = 0;
            for (int count : passengerCounts) {
                totalPassengers += count;
            }

            System.out.println("Planes Served: " + waitingTimes.size());
            System.out.println("Passengers Boarded: " + totalPassengers);
            System.out.println("Maximum Waiting Time: " + max + " ms");
            System.out.println("Minimum Waiting Time: " + min + " ms");
            System.out.println("Average Waiting Time: " + String.format("%.2f", avg) + " ms");
        } else {
            System.out.println("No planes served.");
        }
    }
}
