package com.example.qryde;

public class AvailableRide {
    private String riderUsername;
    private String startLocation;
    private String endLocation;
    private float amountOffered;
    private float distanceAway;

    public AvailableRide(String riderUsername, String startLocation, String endLocation, float amountOffered, float distanceAway){
        this.riderUsername = riderUsername;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.amountOffered = amountOffered;
        this.distanceAway = distanceAway;
    }

    public String getRiderUsername(){
        return this.riderUsername;
    }

    public void setRiderUsername(String riderUsername) {
        this.riderUsername = riderUsername;
    }

    public String getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return this.endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
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
}
