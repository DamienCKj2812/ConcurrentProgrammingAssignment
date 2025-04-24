package com.example.ccpassignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        AirTrafficController atc = new AirTrafficController();
        RefuelTruck refuelTruck = new RefuelTruck();
        List<Plane> planes = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            Plane plane = new Plane("Plane-" + i, atc, refuelTruck);
            planes.add(plane);
            plane.start();
            try {
                Thread.sleep(new Random().nextInt(2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Plane plane : planes) {
            try {
                plane.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        atc.printStatistics();
    }
}