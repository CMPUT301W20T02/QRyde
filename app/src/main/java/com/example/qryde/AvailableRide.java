package com.example.qryde;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class AvailableRide implements Serializable {
    private String riderUsername;
    private String startAddress;
    private String endAddress;
    private double startLocationLat;
    private double startLocationLng;
    private double endLocationLat;
    private double endLocationLng;
    private float amountOffered;
    private float distanceAway;
    private String status;
    private String driver;

    public AvailableRide(String riderUsername, String startAddress, String endAddress, double startLocationLat,
                         double startLocationLng, double endLocationLat, double endLocationLng,
                         float amountOffered, float distanceAway, String status, String driver){
        this.riderUsername = riderUsername;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startLocationLat = startLocationLat;
        this.startLocationLng = startLocationLng;
        this.endLocationLat = endLocationLat;
        this.endLocationLng = endLocationLng;
        this.amountOffered = amountOffered;
        this.distanceAway = distanceAway;
        this.status = "pending";
        this.driver = "";
    }

    public AvailableRide() {

    }

    public String getRiderUsername(){
        return this.riderUsername;
    }

    public void setRiderUsername(String riderUsername) {
        this.riderUsername = riderUsername;
    }

    public String getStartAddress() {
        return this.startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return this.endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public double getStartLocationLat() {
        return this.startLocationLat;
    }

    public void setStartLocationLat() {
        this.startLocationLat = startLocationLat;
    }

    public double getStartLocationLng() {
        return this.startLocationLng;
    }

    public void setStartLocationLng() {
        this.startLocationLng = startLocationLng;
    }


    public double getEndLocationLat() {
        return this.endLocationLat;
    }

    public void setEndLocation() {
        this.endLocationLat = endLocationLat;
    }

    public double getEndLocationLng() {
        return this.endLocationLng;
    }

    public void setEndLocationLng() {
        this.endLocationLng = endLocationLng;
    }

    public float getAmountOffered(){
        return this.amountOffered;
    }

    public void setAmountOffered(float amountOffered) {
        this.amountOffered = amountOffered;
    }

    public float getDistanceAway() {
        return this.distanceAway;
    }

    public void setDistanceAway(float distanceAway) {
        this.distanceAway = distanceAway;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus() {
        this.status = status;
    }

    public String getDriver() {
        return this.driver;
    }

    public void setDriver() {
        this.driver = driver;
    }
}
