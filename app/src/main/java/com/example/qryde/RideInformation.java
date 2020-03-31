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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}

