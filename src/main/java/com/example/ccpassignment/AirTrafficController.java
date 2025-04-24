package com.example.ccpassignment;

import java.util.concurrent.Semaphore;

public class AirTrafficController {
    private final int MAX_PLANES = 3;
    private final Semaphore runway = new Semaphore(1);
    private final Semaphore groundAccess = new Semaphore(MAX_PLANES);

    public boolean requestLanding(Plane plane) {
        if (groundAccess.tryAcquire()) {
            try {
                runway.acquire();
                System.out.println(plane.getName() + ": Granted landing permission.");
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                groundAccess.release();
            }
        }
        return false;
    }

    public void releaseRunway() {
        runway.release();
    }

    public void releaseGate(Plane plane) {
        groundAccess.release();
    }
}
