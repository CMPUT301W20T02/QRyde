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
    private String start;

    /**
     * Constructor for the rideinformation class
     * @param date
     * @param user
     * @param amount
     * @param destination
     * @param start
     */
    public RideInformation(String date, String user, String amount, String start, String destination) {
        this.date = date;
        this.rider = user;
        this.amount = amount;
        this.destination = destination;
        this.start = start;
    }

    /**
     * getter for the date
     * @return the date a ride took place
     */
    public String getDate() {
        return date;
    }

    /**
     * setter for the date
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the username of the rider
     */
    public String getRider() {
        return rider;
    }

    /**
     * Sets the rider username to this activity
     * @param rider
     */
    public void setRider(String rider) {
        this.rider = rider;
    }

    /**
     * @return the amount entered by the rider
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets tha amount entered by the rider to this activity
     * @param amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return the destination string specified by the user
     */
    public String getDestination() {
        return destination;
    }

    /**
     * sets the destination string entered by the user to this activity
     * @param destination
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the start address string entered by the user
     */
    public String getStart() {
        return start;
    }

    /**
     * sets the start string to thsi activity
     * @param start
     */
    public void setStart(String start) {
        this.start = start;
    }
}

