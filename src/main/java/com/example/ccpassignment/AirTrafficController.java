package com.example.ccpassignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class AirTrafficController {
    private final int MAX_PLANES = 3;
    private final Semaphore runway = new Semaphore(1);
    private final Semaphore groundAccess = new Semaphore(MAX_PLANES);
    private final Queue<Integer> availableGates = new LinkedList<>();
    private final Object gateLock = new Object();

    private final List<Long> waitingTimes = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> passengerCounts = Collections.synchronizedList(new ArrayList<>());

    // Initialize gates 
    public AirTrafficController() {
        for (int i = 1; i <= MAX_PLANES; i++) {
            availableGates.offer(i);
        }
    }

    public boolean requestLanding(Plane plane) {
        long requestTime = System.currentTimeMillis();
        System.out.println("ATC: " + plane.getPlaneId() + " requesting landing...");

        int assignedGate = -1;

        try {
            if (plane.isEmergency()) {
                System.out.println("ATC: Emergency landing requested by " + plane.getPlaneId());
                runway.acquire();
                groundAccess.acquire();
            } else {
                if (!groundAccess.tryAcquire()) {
                    System.out.println("ATC: Landing permission denied for " + plane.getPlaneId() + ", Airport Full.");
                    return false;
                }
                runway.acquire();
            }

            synchronized (gateLock) {
                Integer gate = availableGates.poll(); // poll can return null
                if (gate == null) {
                    System.out.println("ATC: No gates available.");
                    runway.release();
                    groundAccess.release();
                    return false;
                }
                assignedGate = gate;
            }

            if (assignedGate == -1) {
                System.out.println("ATC: No gates available.");
                runway.release();
                groundAccess.release();
                return false;
            }

            plane.setAssignedGate(assignedGate); // store it in the plane
            long waitTime = System.currentTimeMillis() - requestTime;
            waitingTimes.add(waitTime);
            passengerCounts.add(plane.getPassengerCount());

            System.out.println("ATC: Landing permission granted for " + plane.getPlaneId());
            System.out.println("ATC: Gate " + assignedGate + " assigned for " + plane.getPlaneId());
            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void releaseRunway() {
        System.out.println("ATC: Runway is now free.");
        runway.release();
    }

    public void releaseGate(Plane plane) {
        int gateToRelease = plane.getAssignedGate();
        if (gateToRelease != -1) {
            synchronized (gateLock) {
                availableGates.offer(gateToRelease); // return gate to queue
            }
        }
        System.out.println("ATC: Gate " + gateToRelease + " released for " + plane.getPlaneId());
        groundAccess.release();
    }

    public List<Integer> getCurrentGatesInUse() {
        List<Integer> inUse = new ArrayList<>();
        synchronized (gateLock) {
            for (int i = 1; i <= MAX_PLANES; i++) {
                if (!availableGates.contains(i)) {
                    inUse.add(i);
                }
            }
        }
        return inUse;
    }

    public void printStatistics() {
        System.out.println("\n=== AIRPORT STATISTICS ===");
        System.out.println("All gates are empty: " + (groundAccess.availablePermits() == MAX_PLANES));

        if (!waitingTimes.isEmpty()) {
            long max = waitingTimes.get(0);
            long min = waitingTimes.get(0);
            long sum = 0;
            for (long t : waitingTimes) {
                if (t > max)
                    max = t;
                if (t < min)
                    min = t;
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
