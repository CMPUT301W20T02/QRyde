package com.example.qryde;

import com.google.maps.model.DirectionsResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RideCalculator {

    private double seconds, minutes, kilometres, cost;

    public RideCalculator(DirectionsResult result) {
        seconds = (double) result.routes[0].legs[0].duration.inSeconds;
        minutes = roundUp(seconds / 60, 1);

        kilometres = result.routes[0].legs[0].distance.inMeters;
        kilometres = roundUp(kilometres / 1000, 2);

        cost = costCalculator(minutes, kilometres);
        cost = roundUp(cost, 2);
    }
    /**
     * calculating the cost for a ride using time and distance
     *
     * @param minutes
     * @param distance
     * @return
     */

    public double costCalculator(double minutes, double distance) {
        double baseCost = 2.00;
        double minimumFare = 4.00;

        double perKm = 0.85;
        double perMinute = 0.25;

        return (baseCost + minimumFare + (minutes * perMinute) + (distance * perKm));
    }

    /**
     * rounding up decimal numbers to a precision point
     *
     * @param number
     * @param precision
     * @return
     */
    public double roundUp(double number, int precision) {
        BigDecimal bd = new BigDecimal(number).setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double getCost() {
        return cost;
    }
    public double getMinutes() {
        return minutes;
    }
    public double getKilometres() {
        return kilometres;
    }
}
