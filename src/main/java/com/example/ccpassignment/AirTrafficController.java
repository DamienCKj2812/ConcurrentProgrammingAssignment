package com.example.ccpassignment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class AirTrafficController {
    private final int MAX_PLANES = 3;
    private final Semaphore runway = new Semaphore(1);
    private final Semaphore groundAccess = new Semaphore(MAX_PLANES);
    private final Queue<Integer> availableGates = new LinkedList<>();
    private final Object gateLock = new Object();

    private final List<Long> waitingTimes = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> passengerCounts = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Integer> planeGateMap = new ConcurrentHashMap<>();

    public AirTrafficController() {
        for (int i = 1; i <= MAX_PLANES; i++) {
            availableGates.offer(i);
        }
    }

    public boolean requestLanding(Plane plane) {
        long requestTime = System.currentTimeMillis();
        System.out.println(plane.getPlaneId() + ": Requesting Landing.");

        int assignedGate = -1;

        try {
            if (plane.isEmergency()) {
                System.out.println("ATC: Emergency landing requested by " + plane.getPlaneId());
                runway.acquire();
                groundAccess.acquire();
            } else {
                if (!groundAccess.tryAcquire()) {
                    return false;
                }
                runway.acquire();
            }

            synchronized (gateLock) {
                Integer gate = availableGates.poll();
                if (gate == null) {
                    System.out.println("ATC: No gates available.");
                    runway.release();
                    groundAccess.release();
                    return false;
                }
                assignedGate = gate;
            }

            if (assignedGate == -1) {
                runway.release();
                groundAccess.release();
                return false;
            }

            plane.setAssignedGate(assignedGate);
            planeGateMap.put(plane.getPlaneId(), assignedGate);

            long waitTime = System.currentTimeMillis() - requestTime;
            waitingTimes.add(waitTime);
            passengerCounts.add(plane.getPassengerCount());

            System.out.println("ATC: Landing permission granted for " + plane.getPlaneId() + ".");
            System.out.println("ATC: Gate-" + assignedGate + " assigned for " + plane.getPlaneId() + ".");
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
                availableGates.offer(gateToRelease);
            }
            planeGateMap.remove(plane.getPlaneId());
            System.out.println("ATC: Gate-" + gateToRelease + " released for " + plane.getPlaneId());
        }
        groundAccess.release();
    }

    public String getAssignedGateString(String planeId) {
        Integer gate = planeGateMap.get(planeId);
        return gate != null ? "Gate-" + gate : "Unknown Gate";
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

            int totalPassengers = passengerCounts.stream().mapToInt(i -> i).sum();

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
