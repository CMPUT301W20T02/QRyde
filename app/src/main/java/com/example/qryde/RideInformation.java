package com.example.qryde;

/**
 * This class defines variables relating to the information
 * of a single drive made by a driver.
 */
public class RideInformation {
    private String date;
    private String rider;
    private String amount;
    private String destination;
    private String distance;
    private String duration;

    /**
     * Constructor for the rideinformation class
     * @param date
     * @param user
     * @param amount
     * @param destination
     * @param distance
     * @param duration
     */
    public RideInformation(String date, String user, String amount, String destination, String distance, String duration) {
        this.date = date;
        this.rider = rider;
        this.amount = amount;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
    }

    /**
     * Getter for distance
     * @return
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Setter for distance
     * @param distance
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     * Getter for date
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Setter for date
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Getter for Rider
     * @return
     */
    public String getRider() {
        return rider;
    }

    /**
     * Setter for Rider
     * @return
     */
    public void setRider(String rider) {
        this.rider = rider;
    }

    /**
     * Getter for Amount
     * @return
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Setter for Amount
     * @return
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * Getter for Destination
     * @return
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Setter for Destination
     * @return
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Getter for Duration
     * @return
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Setter for Duration
     * @return
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }
}

