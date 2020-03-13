package com.example.qryde;

/**
 * class for getters and setter for rider username, start location, end location, amount offered
 * and distance away
 */
public class AvailableRide {
    private String riderUsername;
    private String startLocation;
    private String endLocation;
    private float amountOffered;
    private float distanceAway;

    /**
     * getters and setters for rider username, start location, end location, amount offered
     * and distance away
     * @param riderUsername
     * @param startLocation
     * @param endLocation
     * @param amountOffered
     * @param distanceAway
     */
    public AvailableRide(String riderUsername, String startLocation, String endLocation, float amountOffered, float distanceAway){
        this.riderUsername = riderUsername;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.amountOffered = amountOffered;
        this.distanceAway = distanceAway;
    }

    /**
     * returns rider username
     * @return
     */
    public String getRiderUsername(){
        return this.riderUsername;
    }

    /**
     * sets rider username
     * @param riderUsername
     */
    public void setRiderUsername(String riderUsername) {
        this.riderUsername = riderUsername;
    }

    /**
     * returbs start location
     * @return
     */
    public String getStartLocation() {
        return this.startLocation;
    }

    /**
     * set start location
     * @param startLocation
     */
    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    /**
     * return end location
     * @return
     */
    public String getEndLocation() {
        return this.endLocation;
    }

    /**
     * set end location
     * @param endLocation
     */
    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    /**
     * return amount offered
     * @return
     */
    public float getAmountOffered(){
        return this.amountOffered;
    }

    /**
     * set amount offered
     * @param amountOffered
     */
    public void setAmountOffered(float amountOffered) {
        this.amountOffered = amountOffered;
    }

    /**
     * return distance away
     * @return
     */
    public float getDistanceAway() {
        return this.distanceAway;
    }

    /**
     * set distance away
     * @param distanceAway
     */
    public void setDistanceAway(float distanceAway) {
        this.distanceAway = distanceAway;
    }
}
