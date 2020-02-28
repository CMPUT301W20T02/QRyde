package com.example.qryde;

import java.io.Serializable;

public class ActiveDriver implements Serializable {

    private String activeDriver;
    private double driverLocationLat;
    private double driverLocationLng;
    private String servingRider;

    public ActiveDriver(String activeDriver, double driverLocationLat, double driverLocationLng, String servingRider){
        this.activeDriver = activeDriver;
        this.driverLocationLat = driverLocationLat;
        this.driverLocationLng = driverLocationLng;
        this.servingRider = servingRider;

    }

    public ActiveDriver() {

    }

    public String getActiveDriver() {
        return activeDriver;
    }

    public void setActiveDriver(String activeDriver) {
        this.activeDriver = activeDriver;
    }

    public double getDriverLocationLat() {
        return driverLocationLat;
    }

    public void setDriverLocationLat(double driverLocationLat) {
        this.driverLocationLat = driverLocationLat;
    }

    public double getDriverLocationLng() {
        return driverLocationLng;
    }

    public void setDriverLocationLng(double driverLocationLng) {
        this.driverLocationLng = driverLocationLng;
    }

    public String getServingRider() {
        return servingRider;
    }

    public void setServingRider(String servingRider) {
        this.servingRider = servingRider;
    }




}

